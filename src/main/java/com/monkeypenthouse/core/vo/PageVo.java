package com.monkeypenthouse.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDTO<T> {
    private final List<T> content;
    private final int totalPages;
    private final Long totalContents;
    private final int size;
    private final int page;

    public PageDTO(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalContents = page.getTotalElements();
        this.size = page.getNumberOfElements();
        this.page = page.getNumber() + 1;
    }

    public PageDTO(Page<?> page, List<T> content) {
        this.content = content;
        this.totalPages = page.getTotalPages();
        this.totalContents = page.getTotalElements();
        this.size = page.getNumberOfElements();
        this.page = page.getNumber() + 1;
    }
}
