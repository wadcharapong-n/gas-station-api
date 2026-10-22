package com.station.gas.controller;

import com.station.gas.model.FuelLevel;
import com.station.gas.model.FuelPrice;
import com.station.gas.model.Station;
import com.station.gas.repository.FuelLevelRepository;
import com.station.gas.repository.FuelPriceRepository;
import com.station.gas.repository.FuelTypeRepository;
import com.station.gas.repository.StationRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/stations")
@RequiredArgsConstructor
public class PublicStationController {

    private final StationRepository stationRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final FuelPriceRepository fuelPriceRepository;
    private final FuelLevelRepository fuelLevelRepository;

    @GetMapping
    public ResponseEntity<List<StationSummary>> getAllStations() {
        return ResponseEntity.ok(stationRepository.findAll().stream()
                .map(this::mapToSummary)
                .toList());

    }

    @GetMapping("/{slug}")
    public ResponseEntity<StationDetail> getStationDetails(@PathVariable String slug) {
        return stationRepository.findBySlug(slug)
                .map(this::mapToDetail)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private StationSummary mapToSummary(Station station) {
        return StationSummary.builder()
                .id(station.getId().toString())
                .name(station.getName())
                .slug(station.getSlug())
                .address(station.getAddress())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .isActive(station.getIsActive())
                .build();
    }

    private StationDetail mapToDetail(Station station) {
        List<FuelInfo> fuelInfos = fuelTypeRepository.findByStationAndIsActiveTrue(station).stream()
                .map(ft -> {
                    BigDecimal price = fuelPriceRepository.findTopByStationAndFuelTypeOrderByRecordedAtDesc(station, ft)
                            .map(FuelPrice::getPricePerLitre)
                            .orElse(null);
                    Double level = fuelLevelRepository.findTopByStationAndFuelTypeOrderByRecordedAtDesc(station, ft)
                            .map(FuelLevel::getVolumeLitres)
                            .orElse(null);
                    return FuelInfo.builder()
                            .fuelTypeName(ft.getName())
                            .fuelTypeCode(ft.getCode())
                            .pricePerLitre(price)
                            .volumeLitres(level)
                            .tankCapacityLitres(ft.getTankCapacityLitres())
                            .build();
                })
                .collect(Collectors.toList());

        return StationDetail.builder()
                .id(station.getId().toString())
                .name(station.getName())
                .tagline(station.getTagline())
                .description(station.getDescription())
                .address(station.getAddress())
                .phone(station.getPhone())
//                .hours(station.getHours())
                .fuels(fuelInfos)
                .build();
    }

    @Data
    @Builder
    public static class StationSummary {
        private String id;
        private String name;
        private String slug;
        private String address;
        private Double latitude;
        private Double longitude;
        private Boolean isActive;
    }

    @Data
    @Builder
    public static class StationDetail {
        private String id;
        private String name;
        private String tagline;
        private String description;
        private String address;
        private String phone;
        private String hours;
        private List<FuelInfo> fuels;
    }

    @Data
    @Builder
    public static class FuelInfo {
        private String fuelTypeName;
        private String fuelTypeCode;
        private BigDecimal pricePerLitre;
        private Double volumeLitres;
        private Double tankCapacityLitres;
    }
}
