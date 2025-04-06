package com.store.aladdin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document(collection = "warehouse")
@NoArgsConstructor
public class Warehouse {

    @Id
    private String warehouseId;

    private String name;
    private String address;
    private String pincode;

}
