package com.monkeypenthouse.core.dto.querydsl;

import com.monkeypenthouse.core.entity.Photo;
import com.monkeypenthouse.core.entity.PhotoType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PhotoDTO {
    private String name;
    private PhotoType type;
    private LocalDateTime createdAt;

    @QueryProjection
    public PhotoDTO(String name, PhotoType type, LocalDateTime createdAt) {
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
    }
}
