package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CarouselVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarouselDTO {

    private String url;
    private long amenityId;

    public static CarouselDTO of(CarouselVo vo) {
        return builder()
                .url(vo.getUrl())
                .amenityId(vo.getAmenityId())
                .build();
    }
}
