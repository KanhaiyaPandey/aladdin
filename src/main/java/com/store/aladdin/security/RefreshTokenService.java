package com.store.aladdin.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Opaque refresh tokens, rotated on every use, backed by the Redis instance
 * this app already has wired up (see RedisConfig - previously only used for
 * category caching).
 *
 * Two kinds of record per login session ("family"):
 *  - refresh:token:{sha256(token)} -> {userId, familyId, rotated}
 *  - refresh:family:{familyId}     -> Set<tokenHash> ever issued in this family
 *
 * Rotation: presenting a token that's still "rotated=false" is normal use -
 * it gets marked rotated and a new token/record is issued for the same
 * family. Presenting a token that's already "rotated=true" means it was
 * already used once and is now being replayed (e.g. a stolen cookie) - that
 * revokes the *entire* family, forcing a full re-login, not just this token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    private static final String TOKEN_PREFIX = "refresh:token:";
    private static final String FAMILY_PREFIX = "refresh:family:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String FIELD_USER_ID = "userId";
    private static final String FIELD_FAMILY_ID = "familyId";
    private static final String FIELD_ROTATED = "rotated";

    /** Mints a brand new family + token pair. Used at login/register/OAuth success. */
    public String issue(String userId) {
        String familyId = UUID.randomUUID().toString();
        return issueForFamily(userId, familyId);
    }

    /** Result of a successful rotation: the new refresh token plus whose it is. */
    public record RotationResult(String refreshToken, String userId) {}

    /**
     * Validates + rotates a presented refresh token.
     * @return the new refresh token and the userId it belongs to (the caller
     *         needs the userId to look the user up fresh and mint an
     *         up-to-date access token - roles may have changed since login)
     * @throws InvalidRefreshTokenException if missing, expired, or reused (theft signal)
     */
    public RotationResult rotate(String presentedToken) {
        if (presentedToken == null || presentedToken.isBlank()) {
            throw new InvalidRefreshTokenException("Missing refresh token");
        }

        String tokenHash = hash(presentedToken);
        String tokenKey = TOKEN_PREFIX + tokenHash;

        @SuppressWarnings("unchecked")
        Map<String, Object> record = (Map<String, Object>) redisTemplate.opsForValue().get(tokenKey);
        if (record == null) {
            throw new InvalidRefreshTokenException("Unknown or expired refresh token");
        }

        String userId = (String) record.get(FIELD_USER_ID);
        String familyId = (String) record.get(FIELD_FAMILY_ID);
        boolean rotated = Boolean.TRUE.equals(record.get(FIELD_ROTATED));

        if (rotated) {
            log.warn("Refresh token reuse detected for family {} - revoking entire session", familyId);
            revokeFamily(familyId);
            throw new InvalidRefreshTokenException("Refresh token already used - session revoked");
        }

        record.put(FIELD_ROTATED, true);
        redisTemplate.opsForValue().set(tokenKey, record, remainingTtl(tokenKey));

        String newToken = issueForFamily(userId, familyId);
        return new RotationResult(newToken, userId);
    }

    /** Best-effort revoke of whatever family the presented token belongs to. Used at logout. */
    public void revoke(String presentedToken) {
        if (presentedToken == null || presentedToken.isBlank()) return;

        String tokenKey = TOKEN_PREFIX + hash(presentedToken);
        @SuppressWarnings("unchecked")
        Map<String, Object> record = (Map<String, Object>) redisTemplate.opsForValue().get(tokenKey);
        if (record == null) return;

        revokeFamily((String) record.get(FIELD_FAMILY_ID));
    }

    private String issueForFamily(String userId, String familyId) {
        String token = generateOpaqueToken();
        String tokenHash = hash(token);
        String tokenKey = TOKEN_PREFIX + tokenHash;
        String familyKey = FAMILY_PREFIX + familyId;

        Map<String, Object> record = new HashMap<>();
        record.put(FIELD_USER_ID, userId);
        record.put(FIELD_FAMILY_ID, familyId);
        record.put(FIELD_ROTATED, false);

        Duration ttl = Duration.ofDays(refreshTtlDays);
        redisTemplate.opsForValue().set(tokenKey, record, ttl);
        redisTemplate.opsForSet().add(familyKey, tokenHash);
        redisTemplate.expire(familyKey, ttl);

        return token;
    }

    private void revokeFamily(String familyId) {
        if (familyId == null) return;
        String familyKey = FAMILY_PREFIX + familyId;
        Set<Object> hashes = redisTemplate.opsForSet().members(familyKey);
        if (hashes != null) {
            for (Object h : hashes) {
                redisTemplate.delete(TOKEN_PREFIX + h);
            }
        }
        redisTemplate.delete(familyKey);
    }

    private Duration remainingTtl(String key) {
        Long seconds = redisTemplate.getExpire(key);
        if (seconds == null || seconds <= 0) {
            return Duration.ofDays(refreshTtlDays);
        }
        return Duration.ofSeconds(seconds);
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed available on every JVM; this is unreachable.
            throw new IllegalStateException(e);
        }
    }
}
