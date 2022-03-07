package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.connect.CloudFrontManager;
import com.monkeypenthouse.core.connect.S3Uploader;
import com.monkeypenthouse.core.entity.*;
import com.monkeypenthouse.core.dto.AmenityDTO.*;
import com.monkeypenthouse.core.dto.TicketDTO;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final PhotoRepository photoRepository;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityCategoryRepository amenityCategoryRepository;
    private final DibsRepository dibsRepository;
    private final AmenityRepositoryCustom amenityRepositoryCustom;
    private final UserService userService;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;
    private final CloudFrontManager cloudFrontManager;

    @Override
    @Transactional
    public void add(List<MultipartFile> bannerPhotos, List<MultipartFile> detailPhotos, SaveReqDTO amenityDTO) throws Exception {
        // DB에 저장할 어메니티 객체
        Amenity amenity = modelMapper.map(amenityDTO, Amenity.class);
        amenity.setStartDate(LocalDate.now());
        amenity.setMaxPersonNum(0);
        amenity.setThumbnailName("");
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

        // 티켓 저장
        List<Ticket> tickets = new ArrayList<>();
        LocalDate startDate = null;
        int maxPersonNum = 0;
        for (TicketDTO.saveDTO ticketDTO : amenityDTO.getTickets()) {
            Ticket ticket = modelMapper.map(ticketDTO, Ticket.class);
            if (startDate ==  null || startDate.isAfter(ticketDTO.getEventDateTime().toLocalDate())) {
                startDate = ticketDTO.getEventDateTime().toLocalDate();
            }
            maxPersonNum += ticket.getCapacity();
            ticket.setAmenity(savedAmenity);
            tickets.add(ticket);
        }
        ticketRepository.saveAll(tickets);
        // 어메니티 정보 업데이트
        amenity.setStartDate(startDate);
        amenity.setMaxPersonNum(maxPersonNum);

        List<Photo> photos = new ArrayList<>();
       // 배너 사진 리스트 저장
        for (int i = 0; i < bannerPhotos.size(); i++) {
            String fileName = s3Uploader.upload(bannerPhotos.get(i), "banner");
            if (i == 0) {
                amenity.setThumbnailName("banner/" + fileName);
            }
            Photo photo = Photo
                    .builder()
                    .name(fileName)
                    .type(PhotoType.BANNER)
                    .amenity(savedAmenity)
                    .build();
            photos.add(photo);
        }

        // 상세 사진 리스트 저장
        for (MultipartFile detailPhoto : detailPhotos) {
            String fileName = s3Uploader.upload(detailPhoto, "detail");
            Photo photo = Photo
                    .builder()
                    .name(fileName)
                    .type(PhotoType.DETAIL)
                    .amenity(savedAmenity)
                    .build();
            photos.add(photo);
        }
        photoRepository.saveAll(photos);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailDTO getById(Long id) throws Exception {
        // 어메니티, 카테고리, 사진 정보를 가져와야함
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(Amenity.builder().id(id).build()));
        DetailDTO detailDTO = modelMapper.map(amenity, DetailDTO.class);

        List<AmenityCategory> categories = amenityCategoryRepository.findAllByAmenity(amenity);
        detailDTO.setCategories(categories.stream().map(e -> e.getCategory().getName()).collect(Collectors.toList()));

        List<Photo> photos = photoRepository.findAllByAmenity(amenity);
        detailDTO.setBannerImages(new ArrayList<>());
        detailDTO.setDetailImages(new ArrayList<>());
        // 사진에 대한 signed url 만들기
        for (Photo photo : photos) {
            String filename = photo.getType().name().toLowerCase() + "/" + photo.getName();
            if (photo.getType() == PhotoType.BANNER) {
                detailDTO.getBannerImages().add(cloudFrontManager.getSignedUrlWithCannedPolicy(filename));
            } else {
                detailDTO.getDetailImages().add(cloudFrontManager.getSignedUrlWithCannedPolicy(filename));
            }
        }

        return detailDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Amenity> getAmenitiesDibsOn(final UserDetails userDetails) throws DataNotFoundException {
        final User user = userService.getUserByEmail(userDetails.getUsername());

        return dibsRepository.findAllByUser(user).stream().map(dibs -> dibs.getAmenity()).collect(Collectors.toList());
    }

    @Override
    public Page<ListDTO> getAllByRecommended(Pageable pageable) throws Exception {
        Page<ListDTO> pages = amenityRepositoryCustom.findAllByRecommended(1, pageable);
        for (ListDTO dto : pages.getContent()) {
           String signedUrl =  cloudFrontManager.getSignedUrlWithCannedPolicy(dto.getThumbnailName());
           dto.setThumbnailName(signedUrl);
        }
        return pages;
    }
}
