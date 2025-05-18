package com.store.aladdin.utils.helper;

import org.springframework.stereotype.Service;

@Service
public class Enums {

    public enum Status {
        Active,
        Draft
    }

    public enum StockStatus {
        OUT_OF_STOCK,
        IN_STOCK,
        LIMITED_STOCK
    }

    
}
