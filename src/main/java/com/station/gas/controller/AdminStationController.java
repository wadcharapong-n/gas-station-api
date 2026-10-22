package com.station.gas.controller;

import com.station.gas.model.Station;
import com.station.gas.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/stations")
@RequiredArgsConstructor
public class AdminStationController {

    private final StationRepository stationRepository;

    @GetMapping
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    @PostMapping
    public Station createStation(@RequestBody Station station) {
        return stationRepository.save(station);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable UUID id) {
        return stationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> updateStation(@PathVariable UUID id, @RequestBody Station stationDetails) {
        return stationRepository.findById(id)
                .map(station -> {
                    station.setName(stationDetails.getName());
                    station.setSlug(stationDetails.getSlug());
                    station.setAddress(stationDetails.getAddress());
                    station.setLatitude(stationDetails.getLatitude());
                    station.setLongitude(stationDetails.getLongitude());
                    station.setPhone(stationDetails.getPhone());
                    station.setEmail(stationDetails.getEmail());
                    station.setIsActive(stationDetails.getIsActive());
                    station.setLogoUrl(stationDetails.getLogoUrl());
                    station.setTagline(stationDetails.getTagline());
                    station.setDescription(stationDetails.getDescription());
//                    station.setHours(stationDetails.getHours());
                    return ResponseEntity.ok(stationRepository.save(station));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
