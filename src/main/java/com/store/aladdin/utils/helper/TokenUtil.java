package com.store.aladdin.utils.helper;

import java.util.Arrays;

import org.bson.types.ObjectId;

import com.store.aladdin.utils.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class TokenUtil {

        public static ObjectId extractUserIdFromRequest(HttpServletRequest request) {
        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> "JWT_TOKEN".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (token == null) {
            return null; 
        }

        String userIdStr = JwtUtil.extractUserId(token);
        return new ObjectId(userIdStr);
    }
    
}
