package com.monkeypenthouse.core.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AmenitySimpleVo {
    private Long id;
    private String title;
    private int minPerson;
    private int maxPerson;
    private int currentPerson;
    private String thumbnailName;
}
