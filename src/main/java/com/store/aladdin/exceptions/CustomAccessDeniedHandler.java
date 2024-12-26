package com.store.aladdin.exceptions;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.store.aladdin.utils.ResponseUtil;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @SuppressWarnings("null")
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Set HTTP status code to 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Build the error response
        ResponseEntity<?> responseEntity = ResponseUtil.buildErrorResponse("Access Denied: You do not have permission to access this resource.", HttpStatus.FORBIDDEN);

        // Write the response body
        response.setContentType("application/json");
        response.getWriter().write(responseEntity.getBody().toString());
    }
}
