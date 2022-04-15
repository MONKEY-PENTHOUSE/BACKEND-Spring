package com.monkeypenthouse.core.repository;

import com.monkeypenthouse.core.entity.Amenity;
import com.monkeypenthouse.core.entity.Ticket;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends CrudRepository<Ticket, Long> {

//    @Query("SELECT a FROM Ticket WHERE a.id IN (:ids)")
//    Optional<Ticket> findAllById(@Param("ids") List<Long> ids);
}
