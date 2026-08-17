package com.store.aladdin.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic paged list wrapper so every list endpoint (product search, admin
 * product list, ...) hands the frontend the same shape to page/filter against.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PagedResponse<T> of(List<T> items, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil(totalElements / (double) size) : 0;
        return new PagedResponse<>(items, page, size, totalElements, totalPages);
    }

    public static <T> PagedResponse<T> empty(int page, int size) {
        return new PagedResponse<>(List.of(), page, size, 0, 0);
    }
}
