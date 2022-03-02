package com.monkeypenthouse.core.service;

import com.monkeypenthouse.core.dao.Amenity;
import com.monkeypenthouse.core.dao.Dibs;
import com.monkeypenthouse.core.dao.User;
import com.monkeypenthouse.core.exception.DataNotFoundException;
import com.monkeypenthouse.core.exception.DibsDuplicatedException;
import com.monkeypenthouse.core.repository.AmenityRepository;
import com.monkeypenthouse.core.repository.DibsRepository;
import com.monkeypenthouse.core.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DibsServiceImpl implements DibsService {

    private final UserService userService;
    private final AmenityRepository amenityRepository;
    private final DibsRepository dibsRepository;

    @Override
    @Transactional
    public void createDibs(final UserDetails userDetails, final Long amenityId) throws DataNotFoundException {
        final User user = userService.getUserByEmail(userDetails.getUsername());

        final Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new DataNotFoundException(Amenity.builder().id(amenityId).build()));

        dibsRepository.findByUserAndAmenity(user, amenity).ifPresent((d) -> {
                throw new DibsDuplicatedException();
        });

        final Dibs dibs = new Dibs(user, amenity);

        dibsRepository.save(dibs);
    }
}
