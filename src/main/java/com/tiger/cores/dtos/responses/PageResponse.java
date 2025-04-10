package com.tiger.cores.dtos.responses;

import java.util.Collections;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    int totalPages;
    int pageSize;
    int currentPage;
    long totalElements;

    @Builder.Default
    private List<T> data = Collections.emptyList();
}
