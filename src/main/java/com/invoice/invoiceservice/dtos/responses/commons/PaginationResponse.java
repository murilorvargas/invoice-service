package com.invoice.invoiceservice.dtos.responses.commons;

import java.util.List;

public record PaginationResponse<T>(List<T> data, Pagination pagination) {

    public record Pagination(int page, int pageSize) {}

    public static <T> PaginationResponse<T> of(List<T> data, int page, int pageSize) {
        return new PaginationResponse<>(data, new Pagination(page, pageSize));
    }
}
