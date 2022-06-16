package com.monkeypenthouse.core.service.dto.amenity;

import com.monkeypenthouse.core.controller.dto.amenity.AmenityGetPagesResI;
import com.monkeypenthouse.core.repository.dto.AmenitySimpleDTO;
import com.monkeypenthouse.core.service.dto.PageS;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AmenityGetPagesResS extends PageS<AmenitySimpleResS> {

    public AmenityGetPagesResS(Page<AmenitySimpleDTO> page, List<AmenitySimpleResS> content) {
        super(page, content);
    }

    public AmenityGetPagesResI toI() {
        return new AmenityGetPagesResI(
                getContent().stream().map(AmenitySimpleResS::toI).collect(Collectors.toList()),
                getTotalPages(),
                getTotalContents(),
                getSize(),
                getPage()
        );
    }
}
