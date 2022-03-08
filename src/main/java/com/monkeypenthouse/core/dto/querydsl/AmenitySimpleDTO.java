package com.monkeypenthouse.core.dto.querydsl;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class AmenitySimpleDTO {
    private Long id;
    private String title;
    private int minPerson;
    private int maxPerson;
    private int currentPerson;
    private String thumbnailName;
    private String address;
    private LocalDate startDate;
    private int status;

    @QueryProjection
    public AmenitySimpleDTO(Long id,
                            String title,
                            int minPerson,
                            int maxPerson,
                            int currentPerson,
                            String thumbnailName,
                            String address,
                            LocalDate startDate,
                            int status) {
        this.id = id;
        this.title = title;
        this.minPerson = minPerson;
        this.maxPerson = maxPerson;
        this.currentPerson = currentPerson;
        this.thumbnailName = thumbnailName;
        this.address = address;
        this.startDate = startDate;
        this.status = status;
    }
}
