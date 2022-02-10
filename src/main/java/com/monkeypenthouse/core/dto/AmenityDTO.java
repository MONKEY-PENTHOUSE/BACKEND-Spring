package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.dao.Category;
import lombok.*;

import java.time.LocalDate;
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
        private Category category;
        private List<String> bannerImages;
        private List<String> detailImages;
        private LocalDate deadlineDate;
        private LocalDate startDate;
        private String detail;
        private int recommended;
        private int minPersonNum;
        private int maxPersonNum;
        private int currentPersonNum;
        private int status;
        private int fundingPrice;
        private int dibs;
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
        private int recommended;
        private int minPersonNum;
        private int maxPersonNum;
        private Category category;
        private List<TicketDTO.saveDTO> tickets;
    }
}
