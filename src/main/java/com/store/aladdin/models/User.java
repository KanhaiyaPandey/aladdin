package com.store.aladdin.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.store.aladdin.utils.CartItem;

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
    private ObjectId id;
    
    @Indexed(unique = true)
    private String name;
    
    @Indexed(unique = true)
    private String email;

    private String password;

    @Indexed(unique = true)
    private String phoneNumber;

    private boolean isActive;

    private String profilePicture;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    
     private List<CartItem> cart = new ArrayList<>();

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

}
