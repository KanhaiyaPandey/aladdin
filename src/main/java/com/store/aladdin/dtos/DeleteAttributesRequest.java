package com.store.aladdin.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DeleteAttributesRequest {
    private List<String> attributeIds;
}
