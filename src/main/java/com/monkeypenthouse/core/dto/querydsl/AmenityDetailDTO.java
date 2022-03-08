package com.monkeypenthouse.core.dto.querydsl;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AmenityDetailDTO {
    private Long id;
    private String title;
    private int minPerson;
    private int maxPerson;
    private int currentPerson;
    private String thumbnailName;

    @QueryProjection
    public AmenityDetailDTO(Long id, String title, int minPerson, int maxPerson, int currentPerson, String thumbnailName) {
        this.id = id;
        this.title = title;
        this.minPerson = minPerson;
        this.maxPerson = maxPerson;
        this.currentPerson = currentPerson;
        this.thumbnailName = thumbnailName;
    }
}
