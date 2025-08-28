package com.store.aladdin.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Document(collation = "store")
@NoArgsConstructor
public class Store {

    @Id
    private String storeId;

    private HomePage home;
    private String globalBanner;
    private String globalDescription;
    private String header;
    private String logo;
    private String storeName;
    
    @Data
    @NoArgsConstructor
    public static class HomePage {
    
        private String banner1;
        private String description1;
        private String banner2;
        private String description2;
        private String banner3;
        private String description3;

    }
    
}
