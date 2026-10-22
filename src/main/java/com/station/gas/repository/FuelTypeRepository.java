package com.station.gas.repository;

import com.station.gas.model.FuelType;
import com.station.gas.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuelTypeRepository extends JpaRepository<FuelType, UUID> {
    List<FuelType> findByStationAndIsActiveTrue(Station station);
    List<FuelType> findByStation(Station station);

    Optional<FuelType> findByStationAndCode(Station station, String code);
}
