package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CarouselVo;
import com.monkeypenthouse.core.vo.GetCarouselsResponseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCarouselsResponseDTO {
    private List<CarouselDTO> carousels;

    public static GetCarouselsResponseDTO of(GetCarouselsResponseVo vo) {
        return builder()
                .carousels(
                        vo.getCarouselVos()
                                .stream()
                                .map(carouselVo -> CarouselDTO.of(carouselVo))
                                .collect(Collectors.toList())
                )
                .build();
    }
}
