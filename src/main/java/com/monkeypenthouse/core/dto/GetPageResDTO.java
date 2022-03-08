package com.monkeypenthouse.core.dto;

import com.monkeypenthouse.core.vo.GetPageResponseVo;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetPageResDTO extends PageDTO<AmenitySimpleDTO> {

    public GetPageResDTO(List<AmenitySimpleDTO> content, int totalPages, Long totalContents, int size, int page) {
        super(content, totalPages, totalContents, size, page);
    }
    public static GetPageResDTO of(GetPageResponseVo vo) {
        vo.getContent();
        return new GetPageResDTO(
                vo.getContent()
                        .stream()
                        .map(AmenitySimpleDTO::of)
                        .collect(Collectors.toList()),
                vo.getTotalPages(),
                vo.getTotalContents(),
                vo.getSize(),
                vo.getPage());
    }
}
