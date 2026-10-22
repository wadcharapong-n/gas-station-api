package com.station.gas.repository;

import com.station.gas.model.FuelLevel;
import com.station.gas.model.FuelType;
import com.station.gas.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface FuelLevelRepository extends JpaRepository<FuelLevel, UUID> {
    Optional<FuelLevel> findTopByStationAndFuelTypeOrderByRecordedAtDesc(Station station, FuelType fuelType);
    List<FuelLevel> findByStationOrderByRecordedAtDesc(Station station);
}
