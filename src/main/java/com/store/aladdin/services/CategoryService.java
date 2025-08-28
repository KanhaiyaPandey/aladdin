package com.store.aladdin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.store.aladdin.models.Attribute;
import com.store.aladdin.repository.AttributesRepository;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.store.aladdin.dtos.CategoryResponse;
import com.store.aladdin.exceptions.CustomeRuntimeExceptionsHandler;
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
    private final AttributesRepository attributesRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisCacheService redisCacheService;
    private static final String ALL_ATTRIBUTES_CACHE_KEY = "all-attributes";


    // Find category by ID
    public CategoryResponse getCategoryById(String id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return null;
        }
        Category category = categoryOptional.get();
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, cat -> cat));
        return CategoryMapperUtil.mapToCategoryResponse(category, categoryMap);

    }


    public List<CategoryResponse> getAllCategoryResponses() {
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, cat -> cat));
        return allCategories.stream()
                .filter(cat -> cat.getParentCategoryId() == null)
                .map(cat -> CategoryMapperUtil.mapToCategoryResponse(cat, categoryMap))
                .toList();
    }


    // Save a new category
    public Category createCategory(Category category) {
        Category savedCategory = categoryRepository.save(category);
        List<String> path = new ArrayList<>();
        if (category.getParentCategoryId() != null && !category.getParentCategoryId().isEmpty()) {
            Category parent = categoryRepository.findById(category.getParentCategoryId())
                    .orElseThrow(() -> new CustomeRuntimeExceptionsHandler("Parent category not found"));

            path.addAll(parent.getPath());
            Update update = new Update().addToSet("childCategoryIds", savedCategory.getCategoryId());
            mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(parent.getCategoryId())),
                    update,
                    Category.class
            );
        }
        path.add(savedCategory.getTitle());
        savedCategory.setPath(path);
        categoryRepository.save(savedCategory);
        return savedCategory;
    }





    public void deleteCategoriesByIds(List<String> categoryIds) {
        Set<String> allToDelete = new HashSet<>();
        for (String id : categoryIds) {
            ObjectId objectId = new ObjectId(id);
            collectCategoryAndChildren(objectId.toString(), allToDelete);
        }
        for (String id : allToDelete) {
            categoryRepository.deleteById(id);
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


    public Category updateCategory(String categoryId, Category payload) throws IOException {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            throw new CustomeRuntimeExceptionsHandler("Category not found");
        }
        Category category = optionalCategory.get();
        if (!payload.getTitle().isBlank()) {
            category.setTitle(payload.getTitle());
        }
        if (payload.getDescription() != null && !payload.getDescription().isBlank()) {
            category.setDescription(payload.getDescription());
        }
        if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
            category.setBanner(payload.getBanner());
        }
        return categoryRepository.save(category);
    }


//    save attributes

    public Attribute saveAttribute(Attribute attribute) {
        boolean exists = attributesRepository.existsByName(attribute.getName());
        if (exists) {
            throw new CustomeRuntimeExceptionsHandler("Attribute with name '" + attribute.getName() + "' already exists");
        }
        redisCacheService.delete(ALL_ATTRIBUTES_CACHE_KEY);
        return attributesRepository.save(attribute);
    }

//    get all attributes

    public List<Attribute> gettAllAttributes() {
        List<Attribute> cached = redisCacheService.getList(ALL_ATTRIBUTES_CACHE_KEY, Attribute.class);
        if (cached != null) {
            return cached;
        }
        List<Attribute> attributes = attributesRepository.findAll();
        redisCacheService.set(ALL_ATTRIBUTES_CACHE_KEY, attributes, 300L); // cache for 5 min
        return attributes;
    }

//    update attribute


    public Attribute updateAttribute(String attributeId, Attribute updatedData) {
        Attribute existing = attributesRepository.findById(attributeId)
                .orElseThrow(() -> new CustomeRuntimeExceptionsHandler("Attribute not found with id: " + attributeId));
        if (updatedData.getName() != null && !updatedData.getName().isEmpty()) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getValues() != null && !updatedData.getValues().isEmpty()) {
            existing.setValues(updatedData.getValues());
        }
        redisCacheService.delete(ALL_ATTRIBUTES_CACHE_KEY);
        return attributesRepository.save(existing);
    }

//   delete attributes

    public void deleteAttributes(List<String> attributeIds) {
        try{
            attributesRepository.deleteAllById(attributeIds);
            redisCacheService.delete(ALL_ATTRIBUTES_CACHE_KEY);
        } catch (Exception e) {
            throw new CustomeRuntimeExceptionsHandler("error deleting attributes", e);
        }

    }


}
