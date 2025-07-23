package com.store.aladdin.controllers.AdminController.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.aladdin.models.User;
import com.store.aladdin.services.UserService;


@RestController
@RequestMapping("/api/admin")
public class AllUsers {
    
    @Autowired
    private UserService userService;

        @GetMapping("/users/all-users")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<User>> getAllUsers() {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        }
}
