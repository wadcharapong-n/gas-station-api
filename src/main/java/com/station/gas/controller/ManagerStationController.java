package com.station.gas.controller;

import com.station.gas.model.FuelLevel;
import com.station.gas.model.FuelPrice;
import com.station.gas.model.FuelType;
import com.station.gas.model.Station;
import com.station.gas.model.User;
import com.station.gas.repository.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerStationController {

    private final StationRepository stationRepository;
    private final StationUserRepository stationUserRepository;
    private final UserRepository userRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final FuelPriceRepository fuelPriceRepository;
    private final FuelLevelRepository fuelLevelRepository;

    private User currentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
    }

    private void assertStationAccess(User user, Station station) {
        if (user.getRole() == User.Role.CORPORATE_ADMIN) {
            return;
        }
        boolean assigned = stationUserRepository.findByUser(user).stream()
                .anyMatch(su -> su.getStation().getId().equals(station.getId()));
        if (!assigned) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not assigned to this station");
        }
    }

    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getMyStations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        if (user.getRole() == User.Role.CORPORATE_ADMIN) {
            return ResponseEntity.ok(stationRepository.findAll());
        }
        List<Station> stations = stationUserRepository.findByUser(user).stream()
                .map(su -> su.getStation())
                .collect(Collectors.toList());
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/stations/{stationId}")
    public ResponseEntity<Station> getStation(
            @PathVariable UUID stationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        assertStationAccess(user, station);
        return ResponseEntity.ok(station);
    }

    @PutMapping("/stations/{stationId}")
    public ResponseEntity<Station> updateStation(
            @PathVariable UUID stationId,
            @RequestBody Station stationDetails,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        return stationRepository.findById(stationId)
                .map(station -> {
                    assertStationAccess(user, station);
                    station.setName(stationDetails.getName());
                    station.setSlug(stationDetails.getSlug());
                    station.setAddress(stationDetails.getAddress());
                    station.setLatitude(stationDetails.getLatitude());
                    station.setLongitude(stationDetails.getLongitude());
                    station.setPhone(stationDetails.getPhone());
                    station.setEmail(stationDetails.getEmail());
                    if (user.getRole() == User.Role.CORPORATE_ADMIN) {
                        station.setIsActive(stationDetails.getIsActive());
                    }
                    station.setLogoUrl(stationDetails.getLogoUrl());
                    station.setTagline(stationDetails.getTagline());
                    station.setDescription(stationDetails.getDescription());
//                    station.setHours(stationDetails.getHours());
//                    station.setSocialLinks(stationDetails.getSocialLinks());
//                    station.setServices(stationDetails.getServices());
                    return ResponseEntity.ok(stationRepository.save(station));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stations/{stationId}/fuel-board")
    public ResponseEntity<List<FuelBoardRow>> getFuelBoard(
            @PathVariable UUID stationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        assertStationAccess(user, station);

        List<FuelType> types = fuelTypeRepository.findByStationAndIsActiveTrue(station);
        List<FuelBoardRow> rows = types.stream().map(ft -> {
            var priceOpt = fuelPriceRepository.findTopByStationAndFuelTypeOrderByRecordedAtDesc(station, ft);
            var levelOpt = fuelLevelRepository.findTopByStationAndFuelTypeOrderByRecordedAtDesc(station, ft);
            FuelPrice price = priceOpt.orElse(null);
            FuelLevel level = levelOpt.orElse(null);
            return FuelBoardRow.builder()
                    .fuelTypeId(ft.getId())
                    .name(ft.getName())
                    .code(ft.getCode())
                    .tankCapacityLitres(ft.getTankCapacityLitres())
                    .lowThresholdLitres(ft.getLowThresholdLitres())
                    .isActive(ft.getIsActive())
                    .pricePerLitre(price != null ? price.getPricePerLitre() : null)
                    .priceSource(price != null ? price.getSource().name() : null)
                    .priceRecordedAt(price != null ? price.getRecordedAt() : null)
                    .volumeLitres(level != null ? level.getVolumeLitres() : null)
                    .levelSource(level != null ? level.getSource().name() : null)
                    .levelRecordedAt(level != null ? level.getRecordedAt() : null)
                    .build();
        }).collect(Collectors.toList());
        return ResponseEntity.ok(rows);
    }

    @PostMapping("/stations/{stationId}/prices")
    public ResponseEntity<FuelPrice> overridePrice(
            @PathVariable UUID stationId,
            @RequestBody PriceOverrideRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        Station station = stationRepository.findById(stationId).orElseThrow();
        assertStationAccess(user, station);
        var fuelType = fuelTypeRepository.findById(request.getFuelTypeId()).orElseThrow();
        if (!fuelType.getStation().getId().equals(station.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuel type does not belong to station");
        }

        FuelPrice fuelPrice = FuelPrice.builder()
                .station(station)
                .fuelType(fuelType)
                .pricePerLitre(request.getPrice())
                .source(FuelPrice.Source.MANUAL)
                .overrideActive(true)
                .recordedBy(null)
                .build();

        return ResponseEntity.ok(fuelPriceRepository.save(fuelPrice));
    }

    @PostMapping("/stations/{stationId}/fuel-levels")
    public ResponseEntity<FuelLevel> overrideFuelLevel(
            @PathVariable UUID stationId,
            @RequestBody FuelLevelOverrideRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = currentUser(userDetails);
        Station station = stationRepository.findById(stationId).orElseThrow();
        assertStationAccess(user, station);
        var fuelType = fuelTypeRepository.findById(request.getFuelTypeId()).orElseThrow();
        if (!fuelType.getStation().getId().equals(station.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuel type does not belong to station");
        }

        FuelLevel fuelLevel = FuelLevel.builder()
                .station(station)
                .fuelType(fuelType)
                .volumeLitres(request.getVolumeLitres())
                .source(FuelLevel.Source.MANUAL)
                .overrideActive(true)
                .recordedBy(null)
                .build();

        return ResponseEntity.ok(fuelLevelRepository.save(fuelLevel));
    }

    @Data
    @Builder
    public static class FuelBoardRow {
        private UUID fuelTypeId;
        private String name;
        private String code;
        private Double tankCapacityLitres;
        private Double lowThresholdLitres;
        private Boolean isActive;
        private BigDecimal pricePerLitre;
        private String priceSource;
        private LocalDateTime priceRecordedAt;
        private Double volumeLitres;
        private String levelSource;
        private LocalDateTime levelRecordedAt;
    }

    @Data
    public static class PriceOverrideRequest {
        private UUID fuelTypeId;
        private BigDecimal price;
    }

    @Data
    public static class FuelLevelOverrideRequest {
        private UUID fuelTypeId;
        private Double volumeLitres;
    }
}
