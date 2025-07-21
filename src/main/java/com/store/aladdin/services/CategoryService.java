package com.store.aladdin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.store.aladdin.DTOs.CategoryResponse;
import com.store.aladdin.models.Category;
import com.store.aladdin.models.Product;
import com.store.aladdin.models.Product.ProductCategories;
import com.store.aladdin.repository.CategoryRepository;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.helper.CategoryMapperUtil;
import com.store.aladdin.utils.helper.ProductHelper;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageUploadService imageUploadService;

    @Autowired
    private ProductHelper productHalper;


    // Find category by ID
    public CategoryResponse getCategoryById(ObjectId id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
            if (categoryOptional.isEmpty()) {
               return null;
            }
        Category category = categoryOptional.get();  
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
           .collect(Collectors.toMap(cat -> cat.getCategoryId().toString(), cat -> cat));
        return CategoryMapperUtil.mapToCategoryResponse(category, categoryMap);

    }

    // Find category by title
    public Category getCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

    public List<CategoryResponse> getAllCategoryResponses() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
        .collect(Collectors.toMap(cat -> cat.getCategoryId().toString(), cat -> cat));
        return allCategories.stream()
            .filter(cat -> cat.getParentCategoryId() == null)
            .map(cat -> CategoryMapperUtil.mapToCategoryResponse(cat, categoryMap))
            .collect(Collectors.toList());
    }


    // Save a new category
    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        if (category.getParentCategoryId() != null) {
            ObjectId parentId = new ObjectId(category.getParentCategoryId());
            Optional<Category> parentCategoryOpt = categoryRepository.findById(parentId);

            if (parentCategoryOpt.isPresent()) {
                Category parentCategory = parentCategoryOpt.get();
                if (parentCategory.getChildCategoryIds() == null) {
                    parentCategory.setChildCategoryIds(new ArrayList<>());
                }
                parentCategory.getChildCategoryIds().add(savedCategory.getCategoryId());

                System.out.println(parentCategory.getChildCategoryIds());

                categoryRepository.save(parentCategory);
            } else {
                throw new RuntimeException("Parent category not found with ID: " + category.getParentCategoryId());
            }
        }
        return savedCategory;
    }




    // Add a product to the specified categories
    public void addProductToCategories(Product product, List<String> categoryIds) {
        List<ObjectId> objectIds = categoryIds.stream()
                                            .map(ObjectId::new)
                                            .toList();
        List<Category> categories = categoryRepository.findAllById(objectIds);
        for (Category category : categories) {
            if (!category.getCategoryProducts().contains(product)) {
                category.getCategoryProducts().add(product);
            }
        }
        categoryRepository.saveAll(categories);
    }



    public void deleteCategoriesByIds(List<String> categoryIds) {

        Set<String> allToDelete = new HashSet<>();
        for (String id : categoryIds) {
            ObjectId objectId = new ObjectId(id);
            collectCategoryAndChildren(objectId.toString(), allToDelete);
        }
        for (String id : allToDelete) {
            categoryRepository.deleteById(new ObjectId(id));
        }
        removeCategoriesFromProducts(allToDelete);
    }


    private void collectCategoryAndChildren(String parentId, Set<String> toDelete) {
        String parentIdStr = parentId.toString();
        toDelete.add(parentIdStr);

        List<Category> children = categoryRepository.findByParentCategoryId(parentIdStr);

        for (Category child : children) {
            collectCategoryAndChildren(child.getCategoryId(), toDelete);
        }
    }




    private void removeCategoriesFromProducts(Set<String> deletedCategoryIds) {
        List<Product> allProducts = productRepository.findAll();

        for (Product product : allProducts) {
            boolean modified = false;

            List<ProductCategories> filtered = product.getProductCategories().stream()
                .filter(cat -> !deletedCategoryIds.contains(cat.getCategoryId()))
                .toList();

            if (filtered.size() != product.getProductCategories().size()) {
                product.setProductCategories(new ArrayList<>(filtered));
                modified = true;
            }

            if (modified) {
                productRepository.save(product);
            }
        }
    }



   public Category updateCategory(String categoryId, Category payload, List<String> banners) throws IOException {
    Optional<Category> optionalCategory = categoryRepository.findById(new ObjectId(categoryId));

   

    if (optionalCategory.isEmpty()) {
        throw new RuntimeException("Category not found");
    }

    Category category = optionalCategory.get();


    if (payload.getTitle() != null && !payload.getTitle().isBlank()) {
        category.setTitle(payload.getTitle());
        System.out.println("category title = "+ category.getTitle());
    }

  

    if (payload.getDescription() != null && !payload.getDescription().isBlank()) {
        category.setDescription(payload.getDescription());
           System.out.println("category des = "+ category.getDescription());
    }

    if (banners != null && !banners.isEmpty()) {
        category.setBanner(banners);
    }

    // System.out.println("category"+category);

    return categoryRepository.save(category);
}






}
