package com.store.aladdin.dtos.productDTOs;

import com.store.aladdin.dtos.responseDTOs.CrossSellProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProductsDTO {
    private List<CrossSellProductResponse> crossSellProducts = new ArrayList<>();
    private List<CrossSellProductResponse> upSellProducts    = new ArrayList<>();
}
