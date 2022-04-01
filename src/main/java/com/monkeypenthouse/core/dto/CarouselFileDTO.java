package com.monkeypenthouse.core.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CarouselFileDTO {
    private MultipartFile file;
    private long amenityId;
}
