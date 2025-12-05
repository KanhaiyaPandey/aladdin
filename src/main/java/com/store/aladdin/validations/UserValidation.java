package com.store.aladdin.validations;

import com.store.aladdin.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserValidation {

    private final MongoTemplate mongoTemplate;

    // 🔹 Basic format validations
    private String validateFormat(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return "Name cannot be empty";
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }
        if (user.getPassword() == null || !user.getPassword().matches(
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
        )) {
            return "Password must be at least 8 characters, include an uppercase letter, a number, and a special character";
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}")) {
            return "Phone number must be exactly 10 digits";
        }
        return null;
    }

    // 🔹 Duplicate check in Mongo
    private String validateDuplicates(User user) {

        // Check name
        if (mongoTemplate.exists(
                org.springframework.data.mongodb.core.query.Query.query(
                        Criteria.where("name").is(user.getName())
                ), User.class)) {
            return "User with the same name already exists";
        }

        // Check email
        if (mongoTemplate.exists(
                org.springframework.data.mongodb.core.query.Query.query(
                        Criteria.where("email").is(user.getEmail())
                ), User.class)) {
            return "User with the same email already exists";
        }

        // Check phone number
        if (mongoTemplate.exists(
                org.springframework.data.mongodb.core.query.Query.query(
                        Criteria.where("phoneNumber").is(user.getPhoneNumber())
                ), User.class)) {
            return "User with the same phone number already exists";
        }
        return null;
    }


    // 🔹 Combined validation
    public  String validateUser(User user) {
        String formatError = validateFormat(user);
        if (formatError != null) return formatError;
        return validateDuplicates(user);
    }
}
