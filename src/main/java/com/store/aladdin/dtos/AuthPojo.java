package com.store.aladdin.dtos;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class AuthPojo {
    private String email ;
    private String password;
}
