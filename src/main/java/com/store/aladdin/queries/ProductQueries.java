package com.store.aladdin.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.store.aladdin.dtos.PagedResponse;
import com.store.aladdin.dtos.productDTOs.ProductFilterRequest;
import com.store.aladdin.models.Product;
import com.store.aladdin.services.CategoryService;
import com.store.aladdin.utils.helper.Enums.Status;
import com.store.aladdin.utils.helper.Enums.StockStatus;
import com.store.aladdin.utils.helper.StockHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * All product search/filter/sort/paging is built here as a single MongoDB
 * aggregation pipeline:
 *   1. $match on the "static" criteria (title/tag/sku search, price range,
 *      category incl. subcategories, status).
 *   2. $addFields to compute real stock (totalStock/stockStatus) straight
 *      from warehouseData / variants.variantWarehouseData, so a manually-set,
 *      easily-stale Product.stockStatus is never trusted.
 *   3. an optional $match on that computed stock status.
 *   4. a $facet that sorts/pages the matches and counts the total in the
 *      same round trip.
 *
 * Doing the stock computation in the pipeline (rather than after fetching)
 * is what makes "in stock" / "out of stock" filtering and pagination both
 * correct at the same time - filtering after the fact would either fetch the
 * whole catalogue or return short pages.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductQueries {

    private static final String PRODUCTS_COLLECTION = "products";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_SELL_PRICE = "sellPrice";
    private static final String FIELD_TOTAL_STOCK = "totalStock";
    private static final String FIELD_STOCK_STATUS = "stockStatus";
    private static final String OP_IF_NULL = "$ifNull";

    private final MongoTemplate mongoTemplate;
    private final CategoryService categoryService;

    public PagedResponse<Product> filteredProducts(ProductFilterRequest filter, boolean activeOnly) {
        Optional<List<Criteria>> resolvedCriteria = buildStaticCriteria(filter, activeOnly);
        if (resolvedCriteria.isEmpty()) {
            // Category slug/id didn't resolve to anything - nothing can match.
            return PagedResponse.empty(filter.getPage(), filter.getSize());
        }
        List<Criteria> criteriaList = resolvedCriteria.get();

        StockStatus stockStatusFilter = parseStockStatus(filter.getStockStatus());

        List<AggregationOperation> operations = new ArrayList<>();
        if (!criteriaList.isEmpty()) {
            operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
        }
        operations.addAll(stockAddFieldsStages());
        if (stockStatusFilter != null) {
            operations.add(Aggregation.match(Criteria.where(FIELD_STOCK_STATUS).is(stockStatusFilter.name())));
        }

        int page = filter.getPage();
        int size = filter.getSize();
        FacetOperation facet = Aggregation
                .facet(Aggregation.sort(resolveSort(filter.getSortBy())), Aggregation.skip((long) page * size), Aggregation.limit(size))
                .as("items")
                .and(Aggregation.count().as("count")).as("totalCount");
        operations.add(facet);

        AggregationResults<Document> results = mongoTemplate.aggregate(
                Aggregation.newAggregation(operations), PRODUCTS_COLLECTION, Document.class);
        Document facetDoc = results.getUniqueMappedResult();
        if (facetDoc == null) {
            return PagedResponse.empty(page, size);
        }

        List<Document> itemDocs = facetDoc.getList("items", Document.class, List.of());
        List<Product> products = itemDocs.stream().map(this::toProductWithStock).toList();

        return PagedResponse.of(products, page, size, extractTotalCount(facetDoc));
    }

    /** Empty Optional when a requested category doesn't resolve to any id - nothing can match. */
    private Optional<List<Criteria>> buildStaticCriteria(ProductFilterRequest filter, boolean activeOnly) {
        List<Criteria> criteriaList = new ArrayList<>();

        if (activeOnly) {
            criteriaList.add(Criteria.where("status").is(Status.ACTIVE));
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            String escaped = Pattern.quote(filter.getName().trim());
            criteriaList.add(new Criteria().orOperator(
                    Criteria.where(FIELD_TITLE).regex(".*" + escaped + ".*", "i"),
                    Criteria.where("tags").regex(".*" + escaped + ".*", "i"),
                    Criteria.where("sku").regex(".*" + escaped + ".*", "i")
            ));
        }

        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            Criteria priceCriteria = Criteria.where(FIELD_SELL_PRICE);
            if (filter.getMinPrice() != null) {
                priceCriteria = priceCriteria.gte(filter.getMinPrice());
            }
            if (filter.getMaxPrice() != null) {
                priceCriteria = priceCriteria.lte(filter.getMaxPrice());
            }
            criteriaList.add(priceCriteria);
        }

        if (filter.getCategory() != null && !filter.getCategory().isBlank()) {
            Set<String> categoryIds = categoryService.resolveCategoryIdsForFilter(
                    filter.getCategory(), filter.isIncludeSubcategories());
            if (categoryIds.isEmpty()) {
                return Optional.empty();
            }
            criteriaList.add(Criteria.where("productCategories.categoryId").in(categoryIds));
        }

        return Optional.of(criteriaList);
    }

    private long extractTotalCount(Document facetDoc) {
        List<Document> totalCountDocs = facetDoc.getList("totalCount", Document.class, List.of());
        if (totalCountDocs.isEmpty()) {
            return 0L;
        }
        Number count = totalCountDocs.get(0).get("count", Number.class);
        return count != null ? count.longValue() : 0L;
    }

    private Product toProductWithStock(Document doc) {
        Product product = mongoTemplate.getConverter().read(Product.class, doc);
        Number totalStock = doc.get(FIELD_TOTAL_STOCK, Number.class);
        product.setTotalStock(totalStock != null ? totalStock.intValue() : 0);
        return product;
    }

    private StockStatus parseStockStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return StockStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ Ignoring unknown stockStatus filter value: {}", raw);
            return null;
        }
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sortBy.trim().toLowerCase()) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, FIELD_SELL_PRICE);
            case "price_desc" -> Sort.by(Sort.Direction.DESC, FIELD_SELL_PRICE);
            case "title" -> Sort.by(Sort.Direction.ASC, FIELD_TITLE);
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }

    /**
     * Sums warehouseData / variants[].variantWarehouseData stock into a
     * single `totalStock`, then derives `stockStatus` from it - mirrors
     * {@link StockHelper} but as Mongo aggregation expressions so it can run
     * (and be filtered/sorted on) before pagination, at the database level,
     * across the whole matching set rather than just the current page.
     */
    private List<AggregationOperation> stockAddFieldsStages() {
        Document totalStockField = new Document("$let", new Document()
                .append("vars", new Document()
                        .append("variantStock", new Document("$sum", new Document("$map", new Document()
                                .append("input", new Document(OP_IF_NULL, List.of("$variants", List.of())))
                                .append("as", "v")
                                .append("in", new Document("$sum", new Document("$map", new Document()
                                        .append("input", new Document(OP_IF_NULL,
                                                List.of("$$v.variantWarehouseData", List.of())))
                                        .append("as", "wh")
                                        .append("in", new Document(OP_IF_NULL, List.of("$$wh.stock", 0)))))))))
                        .append("baseStock", new Document("$sum", new Document("$map", new Document()
                                .append("input", new Document(OP_IF_NULL, List.of("$warehouseData", List.of())))
                                .append("as", "wh")
                                .append("in", new Document(OP_IF_NULL, List.of("$$wh.stock", 0)))))))
                .append("in", new Document("$cond", List.of(
                        new Document("$gt", List.of(
                                new Document("$size", new Document(OP_IF_NULL, List.of("$variants", List.of()))), 0)),
                        "$$variantStock",
                        "$$baseStock"
                ))));
        AggregationOperation totalStockStage = context -> new Document("$addFields",
                new Document(FIELD_TOTAL_STOCK, totalStockField));

        Document statusField = new Document("$switch", new Document()
                .append("branches", List.of(
                        new Document("case", new Document("$lte", List.of("$" + FIELD_TOTAL_STOCK, 0)))
                                .append("then", StockStatus.OUT_OF_STOCK.name()),
                        new Document("case", new Document("$lte",
                                List.of("$" + FIELD_TOTAL_STOCK, StockHelper.LOW_STOCK_THRESHOLD)))
                                .append("then", StockStatus.LIMITED_STOCK.name())
                ))
                .append("default", StockStatus.IN_STOCK.name()));
        AggregationOperation statusStage = context -> new Document("$addFields",
                new Document(FIELD_STOCK_STATUS, statusField));

        return List.of(totalStockStage, statusStage);
    }

    // 🔹 Check if a SKU exists
    public boolean doesSkuExist(String sku) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sku").is(sku));
        return mongoTemplate.exists(query, Product.class);
    }
}
