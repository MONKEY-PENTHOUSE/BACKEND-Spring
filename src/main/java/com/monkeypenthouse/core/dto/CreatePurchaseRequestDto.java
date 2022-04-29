package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.CreatePurchaseRequestVo;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CreatePurchaseRequestDto {

    private final List<PurchaseTicketMappingDto> purchaseTicketMappingDtoList;

    public CreatePurchaseRequestVo toVo() {
        return CreatePurchaseRequestVo.builder()
                .purchaseTicketMappingVoList(
                        purchaseTicketMappingDtoList.stream().map(o -> o.toVo()).collect(Collectors.toList()))
                .build();
    }
}
