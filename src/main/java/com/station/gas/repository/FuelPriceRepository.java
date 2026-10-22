package com.station.gas.repository;

import com.station.gas.model.FuelPrice;
import com.station.gas.model.FuelType;
import com.station.gas.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface FuelPriceRepository extends JpaRepository<FuelPrice, UUID> {
    Optional<FuelPrice> findTopByStationAndFuelTypeOrderByRecordedAtDesc(Station station, FuelType fuelType);
    List<FuelPrice> findByStationOrderByRecordedAtDesc(Station station);
}
