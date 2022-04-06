package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.GetTicketsOfAmenityResponseVo;
import com.monkeypenthouse.core.vo.GetViewedResponseVo;
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
public class GetViewedResponseDTO {
    private List<AmenitySimpleDTO> amenities;

    public static GetViewedResponseDTO of(GetViewedResponseVo vo) {
        return builder()
                .amenities(
                        vo.getAmenities()
                                .stream()
                                .map(AmenitySimpleVo -> AmenitySimpleDTO.of(AmenitySimpleVo))
                                .collect(Collectors.toList()))
                .build();
    }
}
