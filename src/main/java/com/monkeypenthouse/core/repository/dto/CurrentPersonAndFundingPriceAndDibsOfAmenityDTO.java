package com.monkeypenthouse.core.repository.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CurrentPersonAndFundingPriceAndDibsOfAmenityDTO {
    private int currentPersonNum;
    private int fundingPrice;
    private Long dibs;

    @QueryProjection
    public CurrentPersonAndFundingPriceAndDibsOfAmenityDTO(int currentPersonNum,
                                                           int fundingPrice,
                                                           Long dibs) {
        this.currentPersonNum = currentPersonNum;
        this.fundingPrice = fundingPrice;
        this.dibs = dibs;
    }
}
