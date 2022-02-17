package com.monkeypenthouse.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.monkeypenthouse.core.dao.Category;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
        @JsonFormat(pattern = "yyyy.MM.dd")
        private LocalDate deadlineDate;
        @JsonFormat(pattern = "yyyy.MM.dd")
        private LocalDate startDate;
        private String detail;
        private int recommended;
        private int minPersonNum;
        private int maxPersonNum;
        private List<String> categories;
        private List<TicketDTO.saveDTO> tickets;
    }
}
