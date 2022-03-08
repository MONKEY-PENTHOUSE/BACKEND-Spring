package com.monkeypenthouse.core.dto.querydsl;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
public class AmenityDetailDTO {
    private Long id;
    private String title;
    private String detail;
    private String address;
    private LocalDate startDate;
    private LocalDate deadlineDate;
    private List<PhotoDTO> photos;
    private List<String> categoryNames;
    private int recommended;
    private int minPersonNum;
    private int maxPersonNum;
    private int currentPersonNum;
    private int status;
    private int fundingPrice;
    private Long dibs;

    @QueryProjection
    public AmenityDetailDTO(Long id,
                            String title,
                            String detail,
                            String address,
                            LocalDate startDate,
                            LocalDate deadlineDate,
                            List<PhotoDTO> photos,
                            List<String> categoryNames,
                            int recommended,
                            int minPersonNum,
                            int maxPersonNum,
                            int currentPersonNum,
                            int status,
                            int fundingPrice,
                            Long dibs) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.address = address;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.photos = photos;
        this.categoryNames = categoryNames;
        this.recommended = recommended;
        this.minPersonNum = minPersonNum;
        this.maxPersonNum = maxPersonNum;
        this.currentPersonNum = currentPersonNum;
        this.status = status;
        this.fundingPrice = fundingPrice;
        this.dibs = dibs;
    }
}
