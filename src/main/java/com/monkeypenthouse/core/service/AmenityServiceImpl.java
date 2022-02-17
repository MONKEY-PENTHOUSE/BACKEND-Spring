package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.connect.S3Uploader;
import com.monkeypenthouse.core.dao.*;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final PhotoRepository photoRepository;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityCategoryRepository amenityCategoryRepository;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SaveReqDTO add(List<MultipartFile> bannerPhotos, List<MultipartFile> detailPhotos, SaveReqDTO amenityDTO) throws Exception {
        System.out.println("amenityDTO = " + amenityDTO);
        // DB에 저장할 어메니티 객체
        Amenity amenity = modelMapper.map(amenityDTO, Amenity.class);
        amenity.setThumbnailName(bannerPhotos.get(0).getName());
        Amenity savedAmenity = amenityRepository.save(amenity);

        List<Category> categories = new ArrayList<>();
        List<AmenityCategory> amenityCategories = new ArrayList<>();
        // 각 카테고리에 대하여
        for (String categoryName : amenityDTO.getCategories()) {
            Optional<Category> optionalCategory = categoryRepository.findByName(categoryName);
            if (optionalCategory.isPresent()) {
                // 존재하면 가져옴
                categories.add(optionalCategory.get());
            } else {
                // 없으면 저장 후 가져옴
                Category category = Category
                        .builder()
                        .name(categoryName)
                        .build();
                categories.add(categoryRepository.save(category));
            }
        }
        // amenityCategory 저장
        for (Category category : categories) {
            AmenityCategory amenityCategory = AmenityCategory
                    .builder()
                    .category(category)
                    .amenity(savedAmenity)
                    .build();
            amenityCategories.add(amenityCategory);
        }
        amenityCategoryRepository.saveAll(amenityCategories);


//       // 배너 사진 리스트 저장
//        for (MultipartFile file : amenityDTO.getBannerPhotos()) {
//            String saveUrl = s3Uploader.upload(file, "banner");
//            Photo photo = Photo
//                    .builder()
//                    .name(file.getName())
//                    .type(PhotoType.BANNER)
//                    .amenity(
//
//
//        }
        // repository에 저장
        return null;
    }

    @Override
    public DetailDTO getById(Long id) throws Exception {
        return null;
    }
}
