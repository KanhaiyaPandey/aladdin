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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.models.Category;
import com.store.aladdin.models.Product;
import com.store.aladdin.models.Product.ProductCategories;
import com.store.aladdin.repository.CategoryRepository;
import com.store.aladdin.repository.ProductRepository;
import com.store.aladdin.utils.helper.CategoryMapperUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
@RequiredArgsConstructor
public class CategoryService {

    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;  
    private final MongoTemplate mongoTemplate;


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
            .toList();
    }


    // Save a new category
    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);

        if (category.getParentCategoryId() != null) {
            ObjectId parentId = new ObjectId(category.getParentCategoryId());
            Update update = new Update().addToSet("childCategoryIds", savedCategory.getCategoryId());
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(parentId)), update, Category.class);
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
        toDelete.add(parentId);
        List<Category> children = categoryRepository.findByParentCategoryId(parentId);
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
    }

  

    if (payload.getDescription() != null && !payload.getDescription().isBlank()) {
        category.setDescription(payload.getDescription());
    }

    if (banners != null && !banners.isEmpty()) {
        category.setBanner(banners);
    }

    // ("category"+category);

    return categoryRepository.save(category);
}






}
