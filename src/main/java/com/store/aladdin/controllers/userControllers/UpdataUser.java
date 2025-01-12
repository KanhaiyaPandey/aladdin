package com.store.aladdin.controllers.userControllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;
import com.store.aladdin.utils.ResponseUtil;

@RestController
@RequestMapping("/api/user")
public class UpdataUser {

    @Autowired
    private UserService userService;
    

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable ObjectId userId, @RequestBody User user) {
        userService.updateUser(userId, user);
        return ResponseUtil.buildResponse("User updated successfully", HttpStatus.OK);
    }

}
