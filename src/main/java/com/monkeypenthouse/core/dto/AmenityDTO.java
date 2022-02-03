package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.dao.Category;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AmenityDTO {

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AmenityDetailDTO {
        private Long id;
        private List<String> bannerPhotos;
        private List<String> detailPhotos;
        private Category category;
        private String title;
        private LocalDate startDateTime;
        private String address;
        private String detail;
        private int achievedRate;
        private int participateNum;
        private int minPersonNum;
        private int maxPersonNum;
        private int paid;
        private LocalDate deadlineDate;
    }
}
