package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.Amenity;
import com.monkeypenthouse.core.dao.AmenityCategory;
import com.monkeypenthouse.core.dao.AmenityCategoryId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AmenityCategoryRepository extends CrudRepository<AmenityCategory, AmenityCategoryId> {
    List<AmenityCategory> findAllByAmenity(Amenity amenity);
}
