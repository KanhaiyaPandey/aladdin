package com.store.aladdin.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.store.aladdin.models.Attribute;
import com.store.aladdin.repository.AttributesRepository;
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

import static com.store.aladdin.keys.CacheKeys.*;


@Service
@RequiredArgsConstructor
public class CategoryService {


    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final AttributesRepository attributesRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisCacheService redisCacheService;


    // Find category by ID
    public CategoryResponse getCategoryById(String id) {
        CategoryResponse cached = redisCacheService.get(SINGLE_CATEGORY_CACHE_KEY + id, CategoryResponse.class);
        if (cached != null) {
            return cached;
        }

        // Only pull this category's own subtree, not the whole collection -
        // getAllCategoryResponses() below is the one place that legitimately
        // needs every category.
        List<Category> subtree = fetchCategorySubtree(id);
        if (subtree.isEmpty()) {
            return null;
        }

        Map<String, Category> categoryMap = subtree.stream()
                .collect(Collectors.toMap(Category::getCategoryId, cat -> cat));
        Category category = categoryMap.get(id);
        if (category == null) {
            return null;
        }

        // ✅ map first, then cache
        CategoryResponse response = CategoryMapperUtil.mapToCategoryResponse(category, categoryMap);
        redisCacheService.set(SINGLE_CATEGORY_CACHE_KEY + id, response, 300L);
        return response;
    }



    public List<CategoryResponse> getAllCategoryResponses() {
        List<CategoryResponse> cached = redisCacheService.getList(ALL_CATEGORIES_CACHE_KEY);
        if(cached != null && !cached.isEmpty()){
           return  cached;
        }
        List<Category> allCategories = categoryRepository.findAll();
        Map<String, Category> categoryMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, cat -> cat));
        List <CategoryResponse> categories = allCategories.stream()
                .filter(this::isRoot)
                .map(cat -> CategoryMapperUtil.mapToCategoryResponse(cat, categoryMap))
                .toList();
        redisCacheService.set(ALL_CATEGORIES_CACHE_KEY, categories, 500L);
        return categories;
    }

    private boolean isRoot(Category category) {
        // Some clients send parentCategoryId as "" rather than omitting it,
        // which used to silently drop the category out of the nav tree.
        return category.getParentCategoryId() == null || category.getParentCategoryId().isBlank();
    }

    /**
     * Breadth-first walk down `childCategoryIds`, starting at `categoryId`,
     * fetching only the nodes actually in this subtree (one query per
     * depth level) instead of loading the entire categories collection.
     */
    private List<Category> fetchCategorySubtree(String categoryId) {
        Optional<Category> rootOptional = categoryRepository.findById(categoryId);
        if (rootOptional.isEmpty()) {
            return List.of();
        }

        List<Category> subtree = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        visited.add(categoryId);
        List<Category> frontier = new ArrayList<>(List.of(rootOptional.get()));

        while (!frontier.isEmpty()) {
            subtree.addAll(frontier);
            List<String> childIds = frontier.stream()
                    .flatMap(cat -> cat.getChildCategoryIds() == null ? Stream.<String>empty()
                            : cat.getChildCategoryIds().stream())
                    .filter(visited::add) // dedupe + guards against a corrupt cycle
                    .toList();
            frontier = childIds.isEmpty() ? List.of() : categoryRepository.findAllById(childIds);
        }

        return subtree;
    }

    /**
     * Resolves a category slug or id, coming from a product filter/search
     * request, to the set of category ids that should match - the category
     * itself plus (when requested) every descendant, so filtering by a
     * parent category ("Men") correctly includes products only tagged with
     * its subcategories ("Men > Shirts").
     */
    public Set<String> resolveCategoryIdsForFilter(String categorySlugOrId, boolean includeSubcategories) {
        if (categorySlugOrId == null || categorySlugOrId.isBlank()) {
            return Collections.emptySet();
        }

        Category root = categoryRepository.findBySlug(categorySlugOrId).orElse(null);
        if (root == null) {
            root = categoryRepository.findById(categorySlugOrId).orElse(null);
        }
        if (root == null) {
            return Collections.emptySet();
        }

        return includeSubcategories
                ? getCategoryIdsWithDescendants(root.getCategoryId())
                : Set.of(root.getCategoryId());
    }

    /**
     * All descendant category ids (inclusive of `categoryId` itself),
     * resolved via the same bounded breadth-first walk as
     * {@link #fetchCategorySubtree}, and cached since it sits on the hot
     * path for every catalogue filter-by-category request.
     */
    public Set<String> getCategoryIdsWithDescendants(String categoryId) {
        if (categoryId == null || categoryId.isBlank()) {
            return Collections.emptySet();
        }

        String cacheKey = CATEGORY_DESCENDANTS_CACHE_KEY + categoryId;
        List<String> cached = redisCacheService.getList(cacheKey);
        if (cached != null && !cached.isEmpty()) {
            return new HashSet<>(cached);
        }

        Set<String> ids = fetchCategorySubtree(categoryId).stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toSet());

        if (!ids.isEmpty()) {
            redisCacheService.set(cacheKey, new ArrayList<>(ids), 300L);
        }
        return ids;
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
        redisCacheService.delete(ALL_CATEGORIES_CACHE_KEY);
        // A new child changes its parent's (and every ancestor's) subtree.
        if (category.getParentCategoryId() != null && !category.getParentCategoryId().isBlank()) {
            redisCacheService.deleteByPrefix(CATEGORY_DESCENDANTS_CACHE_KEY);
        }
        return savedCategory;
    }


//    Delete Categories

    public void deleteCategoriesByIds(List<String> categoryIds) {
        Set<String> allToDelete = new HashSet<>();
        for (String id : categoryIds) {
            collectCategoryAndChildren(id , allToDelete);
        }
        for (String id : allToDelete) {
            categoryRepository.deleteById(id);
            redisCacheService.delete(SINGLE_CATEGORY_CACHE_KEY + id);
        }
        redisCacheService.delete(ALL_CATEGORIES_CACHE_KEY);
        redisCacheService.deleteByPrefix(CATEGORY_DESCENDANTS_CACHE_KEY);
        removeCategoriesFromProducts(allToDelete);
    }


    private void collectCategoryAndChildren(String parentId, Set<String> toDelete) {
        toDelete.add(parentId);
        List<Category> children = categoryRepository.findByParentCategoryId(parentId);
        for (Category child : children) {
            collectCategoryAndChildren(child.getCategoryId(), toDelete);
        }
    }


//    remove category from products

    private void removeCategoriesFromProducts(Set<String> deletedCategoryIds) {
        if (deletedCategoryIds.isEmpty()) {
            return;
        }
        // Only load/save products that actually reference one of the
        // deleted categories, instead of scanning the entire catalogue.
        Query query = Query.query(Criteria.where("productCategories.categoryId").in(deletedCategoryIds));
        List<Product> affectedProducts = mongoTemplate.find(query, Product.class);
        for (Product product : affectedProducts) {
            List<ProductCategories> filtered = product.getProductCategories().stream()
                    .filter(cat -> !deletedCategoryIds.contains(cat.getCategoryId()))
                    .toList();
            product.setProductCategories(new ArrayList<>(filtered));
            productRepository.save(product);
            redisCacheService.delete(SINGLE_PRODUCT_CACHE_KEY + product.getProductId());
        }
    }



//    update category

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
        redisCacheService.delete(SINGLE_CATEGORY_CACHE_KEY + categoryId);
        redisCacheService.delete(ALL_CATEGORIES_CACHE_KEY);
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
        List<Attribute> cached = redisCacheService.getList(ALL_ATTRIBUTES_CACHE_KEY);
        if (cached != null && !cached.isEmpty()) {
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
