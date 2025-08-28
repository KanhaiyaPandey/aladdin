package com.store.aladdin.utils.validation;

import com.store.aladdin.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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
        return null; // ✅ Passed format checks
    }

    // 🔹 Duplicate check in Mongo
    private String validateDuplicates(User user) {
        MatchOperation match = Aggregation.match(
                new Criteria().orOperator(
                        Criteria.where("name").is(user.getName()),
                        Criteria.where("email").is(user.getEmail()),
                        Criteria.where("phoneNumber").is(user.getPhoneNumber())
                )
        );

        Aggregation aggregation = Aggregation.newAggregation(match);
        var results = mongoTemplate.aggregate(aggregation, "users", User.class);

        if (!results.getMappedResults().isEmpty()) {
            return "User with same name, email or phone already exists";
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
