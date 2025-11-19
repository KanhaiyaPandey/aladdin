package com.store.aladdin.models;

import static com.store.aladdin.utils.helper.Enums.*;
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
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String postalCode;
        private String alternateNumber;
        private String country;
        private boolean active; 
        
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
