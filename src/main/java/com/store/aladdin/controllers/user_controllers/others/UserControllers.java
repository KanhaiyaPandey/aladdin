package com.store.aladdin.controllers.user_controllers.others;

import com.store.aladdin.models.Cart;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.USER_BASE;

@RestController
@RequestMapping(USER_BASE)
@RequiredArgsConstructor
@Slf4j
public class UserControllers {

    private UserService userService;
    private AuthService authService;

    public ResponseEntity<Map<String, Object>> crateCart(@RequestBody Cart cart, HttpServletRequest request){
            String token = null;
            String userId = null;
            try {
                token = authService.getToken(request);
                userId = JwtUtil.extractUserId(token);
            } catch (Exception ex) {
                log.warn("⚠️ No valid token found — treating as public user");
            }
            cart.setUserId(userId);
            return ResponseUtil.buildResponse("cart created", HttpStatus.CREATED);
    }

}
