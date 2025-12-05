package com.store.aladdin.dtos.orderDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressDTO {

    @NotBlank(message = "firstName is required")
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

}
