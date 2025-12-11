package com.store.aladdin.controllers.admincontroller.product;

import com.store.aladdin.models.Product;
import com.store.aladdin.services.admin_services.AdminProductService;
import com.store.aladdin.utils.response.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.store.aladdin.routes.AuthRoutes.ADMIN_BASE;

@RestController
@RequestMapping(ADMIN_BASE)
@RequiredArgsConstructor
public class GetProducts {

    private final AdminProductService adminProductService;

       @GetMapping("/product/{productId}")
       public ResponseEntity<Map<String, Object>> getSingleProduct(@PathVariable String productId){
           try {
               Product product = adminProductService.getProductById(productId);
               return ResponseUtil.buildResponse("product fetched", true, product, HttpStatus.OK);
           } catch (Exception e) {
               return ResponseUtil.buildErrorResponse("something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
           }
       }

}
