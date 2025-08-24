package com.store.aladdin.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "attributes")
public class Attribute {

    @Id
    private String id;
    private String name;
    private List<String> values = new ArrayList<>();

}
