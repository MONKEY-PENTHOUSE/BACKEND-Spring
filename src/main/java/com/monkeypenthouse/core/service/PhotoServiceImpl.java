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
import io.lettuce.core.RedisClient;
import lombok.RequiredArgsConstructor;
import org.jets3t.service.CloudFrontServiceException;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final S3Uploader s3Uploader;
    private final CloudFrontManager cloudFrontManager;
    private final AmenityRepository amenityRepository;
    private final PhotoRepository photoRepository;
    private final RedisTemplate redisTemplate;


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
        ListOperations<String, String> listStringOperations = redisTemplate.opsForList();
        ListOperations<String, Long> listLongOperations = redisTemplate.opsForList();

        List<CarouselVo> carouselVos = new ArrayList<>();

        Long size;
        List<String> photoFileNames;
        List<Long> photoAmenityIds;

        if ((size = listStringOperations.size("carouselNames")) > 0) {
            photoFileNames = listStringOperations.range("carouselNames", 0, size);
            photoAmenityIds = listLongOperations.range("carouselAmenityId", 0, size);
        } else {
            photoFileNames = new ArrayList<>();
            photoAmenityIds = new ArrayList<>();
            photoRepository.findAllByType(PhotoType.CAROUSEL)
                    .stream().forEach(e -> {
                        photoFileNames.add(e.getName());
                        photoAmenityIds.add(e.getAmenity() != null? e.getAmenity().getId() : -1);
                    });
            size = Long.valueOf(photoFileNames.size());
        }

        for (int i = 0; i < size; i++) {
            String filename = "carousel/" + photoFileNames.get(i);
            String url = cloudFrontManager.getSignedUrlWithCannedPolicy(filename);
            CarouselVo carouselVo = CarouselVo.builder()
                    .url(url)
                    .amenityId(photoAmenityIds.get(i))
                    .build();
            carouselVos.add(carouselVo);
        }
        return GetCarouselsResponseVo.builder()
                .carouselVos(carouselVos)
                .build();
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    @Transactional
    public void syncCarouselToRedis() {
        ListOperations<String, String> listStringOperations = redisTemplate.opsForList();
        ListOperations<String, Long> listLongOperations = redisTemplate.opsForList();
        photoRepository.findAllByType(PhotoType.CAROUSEL)
                .stream().forEach(e -> {
                            listStringOperations.rightPush("carouselNames", e.getName());
                            listLongOperations.rightPush("carouselAmenityId",
                                    e.getAmenity() != null? e.getAmenity().getId() : -1L);
                        }
                );

    }
}
