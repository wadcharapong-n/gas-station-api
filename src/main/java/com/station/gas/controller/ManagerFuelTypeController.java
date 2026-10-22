package com.station.gas.controller;

import com.station.gas.model.FuelType;
import com.station.gas.model.Station;
import com.station.gas.model.User;
import com.station.gas.repository.FuelTypeRepository;
import com.station.gas.repository.StationRepository;
import com.station.gas.repository.StationUserRepository;
import com.station.gas.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/manager/stations/{stationId}/fuel-types")
@RequiredArgsConstructor
public class ManagerFuelTypeController {

    private final StationRepository stationRepository;
    private final StationUserRepository stationUserRepository;
    private final UserRepository userRepository;
    private final FuelTypeRepository fuelTypeRepository;

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

    private Station resolveStation(UUID stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found"));
    }

    @GetMapping
    public List<FuelType> list(
            @PathVariable UUID stationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Station station = resolveStation(stationId);
        assertStationAccess(currentUser(userDetails), station);
        return fuelTypeRepository.findByStation(station);
    }

    @PostMapping
    public ResponseEntity<FuelType> create(
            @PathVariable UUID stationId,
            @RequestBody FuelTypeWriteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Station station = resolveStation(stationId);
        assertStationAccess(currentUser(userDetails), station);
        validateWriteRequest(request);
        String code = request.getCode().trim();
        if (fuelTypeRepository.findByStationAndCode(station, code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuel code already exists for this station");
        }
        FuelType ft = FuelType.builder()
                .station(station)
                .name(request.getName().trim())
                .code(code)
                .tankCapacityLitres(nz(request.getTankCapacityLitres()))
                .lowThresholdLitres(nz(request.getLowThresholdLitres()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelTypeRepository.save(ft));
    }

    @PutMapping("/{fuelTypeId}")
    public FuelType update(
            @PathVariable UUID stationId,
            @PathVariable UUID fuelTypeId,
            @RequestBody FuelTypeWriteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Station station = resolveStation(stationId);
        assertStationAccess(currentUser(userDetails), station);
        validateWriteRequest(request);
        FuelType ft = fuelTypeRepository.findById(fuelTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fuel type not found"));
        if (!ft.getStation().getId().equals(stationId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuel type does not belong to this station");
        }
        String code = request.getCode().trim();
        Optional<FuelType> codeConflict = fuelTypeRepository.findByStationAndCode(station, code);
        if (codeConflict.isPresent() && !codeConflict.get().getId().equals(fuelTypeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuel code already exists for this station");
        }
        ft.setName(request.getName().trim());
        ft.setCode(code);
        ft.setTankCapacityLitres(nz(request.getTankCapacityLitres()));
        ft.setLowThresholdLitres(nz(request.getLowThresholdLitres()));
        ft.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return fuelTypeRepository.save(ft);
    }

    private static double nz(Double v) {
        return v != null ? v : 0d;
    }

    private void validateWriteRequest(FuelTypeWriteRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code is required");
        }
    }

    @Data
    public static class FuelTypeWriteRequest {
        private String name;
        private String code;
        private Double tankCapacityLitres;
        private Double lowThresholdLitres;
        private Boolean isActive;
    }
}
