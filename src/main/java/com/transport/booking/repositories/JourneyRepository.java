package com.transport.booking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.transport.booking.entities.Journey;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Integer>{
    List<Journey> findByType(Journey.Type type);
}
