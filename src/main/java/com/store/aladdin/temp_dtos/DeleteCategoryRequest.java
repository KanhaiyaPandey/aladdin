package com.store.aladdin.dtos;

import java.util.List;

import lombok.Data;

@Data
public class DeleteCategoryRequest {

  private List<String> categoryIds;
    
}
