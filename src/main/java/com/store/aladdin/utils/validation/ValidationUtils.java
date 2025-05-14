package com.store.aladdin.utils.validation;

import com.store.aladdin.models.User;


public class ValidationUtils {
    public static String validateUser(User user) {
        // Validate name
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return "Name cannot be empty";
        }

        // Validate email
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }

        // Validate password
        if (user.getPassword() == null || !user.getPassword().matches(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        )) {
            return "Password must be at least 8 characters, include an uppercase letter, a number, and a special character";
        }

        // Validate phone number
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}")) {
            return "Phone number must be exactly 10 digits";
        }

        // If all validations pass, return null
        return null;
    }
}
