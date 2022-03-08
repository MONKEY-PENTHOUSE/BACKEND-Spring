package com.monkeypenthouse.core.vo;

import com.monkeypenthouse.core.dto.querydsl.AmenitySimpleDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class GetPageResVo extends PageVo<AmenitySimpleVo> {

    public GetPageResVo(Page<AmenitySimpleVo> page) {
        super(page);
    }

    public GetPageResVo(Page<AmenitySimpleDTO> page, List<AmenitySimpleVo> content) {
        super(page, content);
    }

}
