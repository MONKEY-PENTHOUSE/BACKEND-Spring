package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CreatePurchaseRequestVo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CreatePurchaseRequestDto {

    private final Long amenityId;
    private final List<OrderProductDto> orderProductDtoList;

    public CreatePurchaseRequestVo toVo() {
        return CreatePurchaseRequestVo.builder()
                .amenityId(amenityId)
                .orderProductVoList(orderProductDtoList.stream().map(o -> o.toVo()).collect(Collectors.toList()))
                .build();
    }
}
