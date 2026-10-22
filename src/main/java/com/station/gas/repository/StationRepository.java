package com.station.gas.repository;

import com.station.gas.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface StationRepository extends JpaRepository<Station, UUID> {
    Optional<Station> findBySlug(String slug);
    List<Station> findByIsActiveTrue();
}
