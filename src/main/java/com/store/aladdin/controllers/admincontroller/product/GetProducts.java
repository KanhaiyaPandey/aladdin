package com.store.aladdin.controllers.admincontroller.product;

import com.store.aladdin.dtos.PagedResponse;
import com.store.aladdin.dtos.productDTOs.ProductFilterRequest;
import com.store.aladdin.models.Product;
import com.store.aladdin.routes.ProductRoutes;
import com.store.aladdin.services.admin_services.AdminProductService;
import com.store.aladdin.utils.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ProductRoutes.PRODUCT_BASE)
@RequiredArgsConstructor
public class GetProducts {

    private final AdminProductService adminProductService;

    @GetMapping(ProductRoutes.SINGLE_PRODUCT)
    public ResponseEntity<Map<String, Object>> getSingleProduct(@PathVariable String productId) {
        try {
            Product product = adminProductService.getProductById(productId);
            return ResponseUtil.buildResponse("product fetched", true, product, HttpStatus.OK);
        } catch (Exception e) {
            log.error("❌ Error fetching product {}: {}", productId, e.getMessage());
            return ResponseUtil.buildErrorResponse("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /**
     * Admin product list/search - identical filters to the public catalogue
     * (see PublicControllers#getAllProducts) minus the ACTIVE-only
     * restriction, so drafts and out-of-stock products stay visible for
     * management, sorting and paging.
     */
    @GetMapping(ProductRoutes.ALL_PRODUCTS)
    public ResponseEntity<Map<String, Object>> getAllProducts(@ModelAttribute ProductFilterRequest filter) {
        try {
            PagedResponse<Product> products = adminProductService.getFilteredProducts(filter);
            return ResponseUtil.buildResponse("products fetched successfully", true, products, HttpStatus.OK);
        } catch (Exception e) {
            log.error("❌ Error fetching products: {}", e.getMessage());
            return ResponseUtil.buildErrorResponse("Error fetching products", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
