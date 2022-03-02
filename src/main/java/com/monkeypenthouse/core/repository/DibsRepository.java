package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DibsRepository extends JpaRepository<Dibs, DibsId> {

    Optional<Dibs> findByUserAndAmenity(User user, Amenity amenity);
}
