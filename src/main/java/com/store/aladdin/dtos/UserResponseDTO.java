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
    private User.Cart cart = new User.Cart();
    private List<User.Address> addresses = new ArrayList<>();


    public UserResponseDTO(User user) {
        this.name = user.getName();
        this.id = user.getId();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.isActive = user.isActive();
        this.profilePicture = user.getProfilePicture();
        this.cart = user.getCart();
        this.addresses = user.getAddresses();
    }

}
