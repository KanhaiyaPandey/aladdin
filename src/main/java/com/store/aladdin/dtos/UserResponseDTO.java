package com.store.aladdin.dtos;

import com.store.aladdin.models.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserResponseDTO {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean isActive;
    private String profilePicture;
    private List<User.CartItems> cartItems;
    private List<User.Address> addresses;
    private List<String> roles;


    public UserResponseDTO(User user, boolean isAdmin) {
        this.name = user.getName();
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.isActive = user.isActive();
        this.profilePicture = user.getProfilePicture();
        this.cartItems = user.getCartItems();
        this.addresses = user.getAddresses();
        if(isAdmin){
            this.roles = user.getRoles();
        }
    }

}
