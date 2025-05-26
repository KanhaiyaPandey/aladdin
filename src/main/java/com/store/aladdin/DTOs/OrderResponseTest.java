package com.store.aladdin.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseTest {
    private String id;
    private int amount;
    private String currency;
    private String receipt;
}
