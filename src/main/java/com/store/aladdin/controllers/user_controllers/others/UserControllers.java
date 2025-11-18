package com.store.aladdin.controllers.user_controllers.others;

import com.store.aladdin.dtos.UserResponseDTO;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.User;
import com.store.aladdin.services.AuthService;
import com.store.aladdin.services.ImageUploadService;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.JwtUtil;
import com.store.aladdin.utils.response.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.USER_BASE;
import static com.store.aladdin.routes.UserRoutes.*;

@RestController
@RequestMapping(USER_BASE)
@RequiredArgsConstructor
@Slf4j
public class UserControllers {

    private final UserService userService;
    private final ImageUploadService imageUploadService;
    private final AuthService authService;

    @PutMapping(UPDATE_USER)
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user, HttpServletRequest request){
                UserResponseDTO updatedUser = userService.updateUser(user);
                return ResponseUtil.buildResponse("user updated", true, updatedUser, HttpStatus.OK);
    }

    @PostMapping(value = "/update-profile-image", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> uploadMultipleMedia(@RequestParam("media") MultipartFile[] files, HttpServletRequest request ) {
        String token  = authService.getToken(request);
        String userId = JwtUtil.extractUserId(token);
        User user     = userService.getUserById(userId);
        try{
            String mediaUrl = imageUploadService.uploadImage(files[0]);
            user.setProfilePicture(mediaUrl);
        } catch (IOException e) {
            throw new CustomeRuntimeExceptionsHandler("something went wrong"+e);
        }
        UserResponseDTO updatedUser = userService.updateUser(user);
        return ResponseUtil.buildResponse("profile image updated", true, updatedUser, HttpStatus.OK);
    }



}
