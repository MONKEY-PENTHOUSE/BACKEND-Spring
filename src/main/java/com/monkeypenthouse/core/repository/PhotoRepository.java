package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.dao.Photo;
import org.springframework.data.repository.CrudRepository;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
}
