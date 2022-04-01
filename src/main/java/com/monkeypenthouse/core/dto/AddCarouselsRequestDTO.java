package com.monkeypenthouse.core.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class AddCarouselsRequestDTO {
    private List<CarouselFileDTO> carousels;
}
