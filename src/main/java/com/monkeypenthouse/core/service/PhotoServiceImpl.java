package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.connect.CloudFrontManager;
import com.monkeypenthouse.core.connect.S3Uploader;
import com.monkeypenthouse.core.constant.ResponseCode;
import com.monkeypenthouse.core.dto.AddCarouselsRequestDTO;
import com.monkeypenthouse.core.dto.CarouselFileDTO;
import com.monkeypenthouse.core.entity.Amenity;
import com.monkeypenthouse.core.entity.Photo;
import com.monkeypenthouse.core.entity.PhotoType;
import com.monkeypenthouse.core.exception.CommonException;
import com.monkeypenthouse.core.repository.AmenityRepository;
import com.monkeypenthouse.core.repository.PhotoRepository;
import com.monkeypenthouse.core.vo.CarouselVo;
import com.monkeypenthouse.core.vo.GetCarouselsResponseVo;
import lombok.RequiredArgsConstructor;
import org.jets3t.service.CloudFrontServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final S3Uploader s3Uploader;
    private final CloudFrontManager cloudFrontManager;
    private final AmenityRepository amenityRepository;
    private final PhotoRepository photoRepository;


    @Override
    @Transactional
    public void addCarousels(final AddCarouselsRequestDTO addReqDTO) throws IOException {
        List<Photo> photos = new ArrayList<>();
        for (CarouselFileDTO carousel : addReqDTO.getCarousels()) {
            String fileName = s3Uploader.upload(carousel.getFile(), "carousel");
            long amenityId = carousel.getAmenityId();
            Amenity amenity = null;
            if (amenityId != 0) {
                amenity = amenityRepository.findById(amenityId)
                        .orElseThrow(() -> new CommonException(ResponseCode.DATA_NOT_FOUND));
            }
            photos.add(Photo
                    .builder()
                    .name(fileName)
                    .type(PhotoType.CAROUSEL)
                    .amenity(amenity)
                    .build());
        }
        photoRepository.saveAll(photos);
    }

    @Override
    @Transactional(readOnly = true)
    public GetCarouselsResponseVo getCarousels() throws CloudFrontServiceException, IOException {
        List<CarouselVo> carouselVos = new ArrayList<>();
        List<Photo> photos = photoRepository.findAllByType(PhotoType.CAROUSEL);
        for (Photo photo : photos) {
            String filename = photo.getType().name().toLowerCase() + "/" + photo.getName();
            String url = cloudFrontManager.getSignedUrlWithCannedPolicy(filename);
            CarouselVo carouselVo = CarouselVo.builder()
                    .url(url)
                    .amenityId(photo.getAmenity() != null? photo.getAmenity().getId() : -1)
                    .build();
            carouselVos.add(carouselVo);
        }
        return GetCarouselsResponseVo.builder()
                .carouselVos(carouselVos)
                .build();
    }
}
