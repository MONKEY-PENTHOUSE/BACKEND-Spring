package com.monkeypenthouse.core.common;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageWrapper<T> {
    private final List<T> content;
    private final int totalPages;
    private final Long totalContents;
    private final int size;
    private final int page;

    public PageWrapper(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalContents = page.getTotalElements();
        this.size = page.getNumberOfElements();
        this.page = page.getNumber();
    }
}
