package com.store.aladdin.controllers.userControllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;

@RestController
@RequestMapping("/api/user")
public class GetUser {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
  public ResponseEntity<?> getUserById(@PathVariable ObjectId userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated.");
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String loggedInUserEmail = userDetails.getUsername();
    User loggedInUser = userService.getUserByEmail(loggedInUserEmail);
    if (loggedInUser == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
    }
    if (!loggedInUser.getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to access this resource.");
    }
    User user = userService.getUserById(userId);
    return ResponseEntity.ok(user);
}

    
}
