package com.damian.xBank.shared.infrastructure.web.dto.response;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
}