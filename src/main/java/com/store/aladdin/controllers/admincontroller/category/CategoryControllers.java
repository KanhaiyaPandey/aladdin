package com.store.aladdin.controllers.admincontroller.category;


import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
import com.store.aladdin.models.Attribute;
import com.store.aladdin.routes.CategoryRoutes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.aladdin.dtos.DeleteCategoryRequest;
import com.store.aladdin.dtos.DeleteAttributesRequest;
import com.store.aladdin.models.Category;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.services.ImageUploadService;
import com.store.aladdin.utils.helper.ProductHelper;
import com.store.aladdin.utils.response.ResponseUtil;
import com.store.aladdin.validations.CategoryValidation;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(CategoryRoutes.CATEGORY_BASE)
@RequiredArgsConstructor
public class CategoryControllers {


    private final ImageUploadService imageUploadService;
    private final CategoryService categoryService;
    private final ProductHelper productHalper;
    private final CategoryValidation categoryValidation;




    // create category

    
    @PostMapping(value = CategoryRoutes.CREATE_CATEGORY, consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> createCategory(
    @RequestParam("category") String categoryJson,
    @RequestPart(value = "banner", required = false) List<MultipartFile> bannerImages) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Category category = objectMapper.readValue(categoryJson, Category.class);
            categoryValidation.validateCategory(category);
            if (category.getParentCategoryId() != null && !category.getParentCategoryId().isEmpty()) {
                categoryValidation.checkSubCategoryName(category.getTitle(), category.getParentCategoryId());
            }
            category.setSlug(generateSlug(category.getTitle()));
            List<String> bannerUrls = productHalper.uploadImages(bannerImages, imageUploadService);
            category.setBanner(bannerUrls);
            Category savedCategory = categoryService.createCategory(category);
            return ResponseUtil.buildResponse("Category created successfully", true, savedCategory, HttpStatus.CREATED);

        } catch (IOException e) {
            throw new CustomeRuntimeExceptionsHandler("Invalid category JSON format: " + e.getMessage(), e);
        }
    }



    // update category

    @PutMapping(value = CategoryRoutes.UPDATE_CATEGORY, consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategory(
        @PathVariable String categoryId,
        @RequestParam("category") String categoryJson,
        @RequestPart(value = "banner", required = false) List<MultipartFile> banner
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Category categoryPayload = objectMapper.readValue(categoryJson, Category.class);
            categoryValidation.validateCategory(categoryPayload);
            categoryPayload.setSlug(generateSlug(categoryPayload.getTitle()));
            List<String> bannerUrls = productHalper.uploadImages(banner, imageUploadService);
           Category updatedCategory =  categoryService.updateCategory(categoryId, categoryPayload, bannerUrls);
            return ResponseUtil.buildResponse("Category updated successfully", true, updatedCategory, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error updating category", HttpStatus.INTERNAL_SERVER_ERROR,  e.getMessage());
        }
    }




    // delete category

    
    @DeleteMapping(value = CategoryRoutes.UPDATE_CATEGORY)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCategories(@RequestBody DeleteCategoryRequest request){
        try {
            List<String> categoryIds = request.getCategoryIds();
            if (categoryIds == null || categoryIds.isEmpty()) {
                return ResponseUtil.buildResponse("No category IDs provided", false, null, HttpStatus.BAD_REQUEST);
            }
            categoryService.deleteCategoriesByIds(categoryIds);
            return ResponseUtil.buildResponse("Categories deleted successfully", true, null, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error deleting categories", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }


//    attributes

    @PostMapping(value = CategoryRoutes.CREATE_ATTRIBUTE)
    public ResponseEntity<Map<String, Object>> createAttributes(@RequestBody Attribute attributes){
        try{
           Attribute savedAttribute = categoryService.saveAttribute(attributes);
           return ResponseUtil.buildResponse("Attribute added", true, savedAttribute, HttpStatus.OK);
        }catch (Exception e){
            return ResponseUtil.buildErrorResponse("Error creating attribute", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/all-attributes")
    public ResponseEntity<Map<String, Object>> allAttributes(){
        try{
           List <Attribute> attributes = categoryService.gettAllAttributes();
            return ResponseUtil.buildResponse("Attributes fetched successfully", true, attributes, HttpStatus.OK);
        }catch (Exception e){
            return ResponseUtil.buildErrorResponse("Error fetching attributes", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = CategoryRoutes.UPDATE_ATTRIBUTE)
    public ResponseEntity<Map<String,Object>> updateAttribute( @PathVariable String attributeId, @RequestBody Attribute attribute){
       try{
           Attribute updated = categoryService.updateAttribute(attributeId, attribute);
           return  ResponseUtil.buildResponse("attribute updated successfully", true, updated, HttpStatus.OK);
       } catch (Exception e) {
           return ResponseUtil.buildErrorResponse("Error updating attribute", HttpStatus.INTERNAL_SERVER_ERROR);

       }

    }

    @DeleteMapping(value = CategoryRoutes.DELETE_ATTRIBUTES)
    public  ResponseEntity<Map<String,Object>> deleteAttributes(@RequestBody DeleteAttributesRequest request){
        try{
             categoryService.deleteAttributes(request.getAttributeIds());
            return  ResponseUtil.buildResponse("attributes deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseUtil.buildErrorResponse("Error deleting attribute", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //  slug method

        private String generateSlug(String title) {
        return title.trim()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "") 
                    .replaceAll("\\s+", "-");  
    }
    
}
