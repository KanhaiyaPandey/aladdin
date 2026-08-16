package com.store.aladdin.security;

/**
 * Thrown by {@link RefreshTokenService} when a refresh token is missing,
 * expired, or has already been rotated (i.e. it's being replayed - see
 * RefreshTokenService for what happens in that case).
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
