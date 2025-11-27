package com.store.aladdin.exceptions;

import com.store.aladdin.models.User;
import com.store.aladdin.repository.UserRepository;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
       private final UserService userService;
       private final AuthService authService;
       private final UserRepository userRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    public CustomOAuth2SuccessHandler(@Lazy UserService userService, @Lazy AuthService authService, @Lazy UserRepository userRepository) {
        this.userService = userService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        User user;
        try {
            user = userService.getUserByEmail(email);
            authService.setCookie(user, response);
        } catch (RuntimeException e) {
            userService.saveUserByOauth(email, name, response, picture);
        }
        String redirectTo = (String) request.getSession().getAttribute("redirect_uri");
        log.info("Session redirectTo = {}", redirectTo);
        if (redirectTo != null) {
            request.getSession().removeAttribute("redirect_uri"); // cleanup
            response.sendRedirect(frontendUrl + redirectTo);
            return;
        }
        response.sendRedirect(frontendUrl);
     }
}
