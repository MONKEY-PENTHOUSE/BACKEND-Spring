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
    public static class DetailDTO {
        private Long id;
        private String title;
        private String address;
        private LocalDate deadlineDate;
        private LocalDate startDate;
        private String detail;
        private int minPersonNum;
        private int maxPersonNum;
        private int status;
        private Category category;
        private List<String> bannerPhotos;
        private List<String> detailPhotos;
        private int participateNum;
        private int paid;
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SaveReqDTO {
        private String title;
        private String address;
        private LocalDate deadlineDate;
        private String thumbnailName;
        private List<String> bannerPhotos;
        private List<String> detailPhotos;
        private String detail;
        private boolean recommended;
        private int minPersonNum;
        private int maxPersonNum;
        private Category category;
        private List<TicketDTO.saveDTO> tickets;
    }
}
