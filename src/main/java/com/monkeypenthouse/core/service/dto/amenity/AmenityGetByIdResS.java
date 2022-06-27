package com.monkeypenthouse.core.service.dto.amenity;

import com.monkeypenthouse.core.controller.dto.amenity.AmenityGetByIdResI;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class AmenityGetByIdResS {
    private final Long id;
    private final String title;
    private final String detail;
    private final String address;
    private final LocalDate startDate;
    private final LocalDate deadlineDate;
    private final List<String> bannerImages;
    private final List<String> detailImages;
    private final List<String> categories;
    private final int recommended;
    private final int minPersonNum;
    private final int maxPersonNum;
    private final int currentPersonNum;
    private final int status;
    private final int fundingPrice;
    private final Long dibs;

    public AmenityGetByIdResI toI() {
        return new AmenityGetByIdResI(
                id,
                title,
                detail,
                address,
                startDate,
                deadlineDate,
                bannerImages,
                detailImages,
                categories,
                recommended,
                minPersonNum,
                maxPersonNum,
                currentPersonNum,
                status,
                fundingPrice,
                dibs
        );
    }
}
