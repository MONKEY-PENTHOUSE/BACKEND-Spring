package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.Amenity;
import com.monkeypenthouse.core.dao.Photo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
    List<Photo> findAllByAmenity(Amenity amenity);
}
