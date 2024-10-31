package com.store.aladdin.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.store.aladdin.utils.CartItem;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private ObjectId id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    
     private List<CartItem> cart = new ArrayList<>();

    private List<ObjectId> orders = new ArrayList<>();

}
