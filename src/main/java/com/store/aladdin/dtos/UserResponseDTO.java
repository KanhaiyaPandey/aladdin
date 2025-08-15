package com.store.aladdin.dtos;

import org.bson.types.ObjectId;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private ObjectId id;
    private String name;
    private String email;    
}
