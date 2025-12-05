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
import java.util.List;
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

//    update user

    @PutMapping(UPDATE_USER)
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody User user, HttpServletRequest request){
                UserResponseDTO updatedUser = userService.updateUser(user);
                return ResponseUtil.buildResponse("user updated", true, updatedUser, HttpStatus.OK);
    }

//    update user's address

    @PutMapping(UPDATE_ADDRESS)
    public ResponseEntity<Map<String, Object>> updateAddress(@RequestBody User.Address address, HttpServletRequest request){
         try{
             String token  = authService.getToken(request);
             String userId = JwtUtil.extractUserId(token);
             User user     = userService.getUserById(userId);
             User updatedUser = userService.updateAddress(user, address);
             return ResponseUtil.buildResponse("address updated", true, updatedUser, HttpStatus.OK);
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
    }

//    add address

    @PostMapping(ADD_ADDRESS)
    public ResponseEntity<Map<String, Object>> addAddress(@RequestBody User.Address address, HttpServletRequest request){
        try{
            String token  = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);
            User user     = userService.getUserById(userId);
            User updatedUser = userService.addAddress(user, address);
            return ResponseUtil.buildResponse("address added", true, updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    update profile image

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

//    update cart

    @PutMapping(UPDATE_CART)
    public ResponseEntity<Map<String, Object>>updateCart(@RequestBody List<User.CartItems> items, HttpServletRequest request){
        String token  = authService.getToken(request);
        String userId = JwtUtil.extractUserId(token);
        User user     = userService.getUserById(userId);
        user.setCartItems(items);
        UserResponseDTO updatedUser = userService.updateUser(user);
        return ResponseUtil.buildResponse("profile image updated",true, updatedUser, HttpStatus.OK);
    }


// delete address


    @DeleteMapping(DELETE_ADDRESS)
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @RequestParam String addressId,
            HttpServletRequest request
    ) {
        try {
            String token  = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            User updated = userService.deleteAddress(userId, addressId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Address deleted successfully",
                    "user", updated
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }



    @PutMapping(SET_ADDRESS_DEFAULT)
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @RequestParam String addressId,
            HttpServletRequest request
    ) {
        try {
            String token  = authService.getToken(request);
            String userId = JwtUtil.extractUserId(token);

            User updated = userService.setDefaultAddress(userId, addressId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Default address updated",
                    "user", updated
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }







}
