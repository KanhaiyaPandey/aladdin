package com.store.aladdin.models;

import static com.store.aladdin.utils.helper.Enums.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;
    
    @Indexed(unique = true)
    private String name;
    
    @Indexed(unique = true)
    private String email;

    private String password;

    @Indexed(unique = true)
    private String phoneNumber;

    private boolean isActive;
    private String profilePicture;
    private RiskStatus riskStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private List<CartItems> cartItems = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
    private List<Address> addresses = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Address {
        private String addressId;
        private String firstName;
        private String lastName;

        @NotBlank(message = "houseNumber / street is required")
        private String houseNumber;

        @NotBlank(message = "area is required")
        private String area;

        @NotBlank(message = "city is required")
        private String city;

        @NotBlank(message = "state is required")
        private String state;

        @NotBlank(message = "pincode is required")
        @Pattern(regexp = "\\d{6}", message = "pincode must be 6 digits")
        private String pincode;

        @NotBlank(message = "email is required")
        @Pattern(regexp = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,6}$", message = "invalid email")
        private String email;

        @NotBlank(message = "phoneNumber is required")
        @Pattern(regexp = "\\d{10}", message = "phoneNumber must be 10 digits")
        private String phoneNumber;

        private boolean isDefault = false;
        
    }



    @Data
    @NoArgsConstructor
    public static class CartItems{
        private String productId;
        private String variantId;
        private String title;
        private String image;
        private List<String> attributes;
        private List<String> options;
        private Integer quantity;
        private Double price;
    }

}
