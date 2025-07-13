package com.store.aladdin.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Document(collection = "warehouse")
@NoArgsConstructor
public class Warehouse {

    @Id
    private String warehouseId;

    @NotBlank(message = "Warehouse name is required")
    @Size(min = 3, max = 100, message = "Warehouse name must be between 3 and 100 characters")
    @Indexed(unique = true)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, message = "Address must be at least 5 characters long")
    private String address;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Pincode must be a valid 6-digit Indian pincode")
    private String pincode;

}
