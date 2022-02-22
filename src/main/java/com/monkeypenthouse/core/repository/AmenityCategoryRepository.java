package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.AmenityCategory;
import com.monkeypenthouse.core.dao.AmenityCategoryId;
import org.springframework.data.repository.CrudRepository;

public interface AmenityCategoryRepository extends CrudRepository<AmenityCategory, AmenityCategoryId> {
}
