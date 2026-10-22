package com.station.gas.controller;

import com.station.gas.model.User;
import com.station.gas.model.StationUser;
import com.station.gas.model.Station;
import com.station.gas.repository.UserRepository;
import com.station.gas.repository.StationUserRepository;
import com.station.gas.repository.StationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final StationUserRepository stationUserRepository;
    private final StationRepository stationRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();
        return userRepository.save(user);
    }

    @PostMapping("/assign")
    public ResponseEntity<StationUser> assignManagerToStation(@RequestBody AssignmentRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Station station = stationRepository.findById(request.getStationId()).orElseThrow();
        
        StationUser assignment = StationUser.builder()
                .user(user)
                .station(station)
                .assignedBy(null) // Should be current admin ID
                .build();
        
        return ResponseEntity.ok(stationUserRepository.save(assignment));
    }

    @Data
    public static class CreateUserRequest {
        private String name;
        private String email;
        private String password;
        private User.Role role;
    }

    @Data
    public static class AssignmentRequest {
        private UUID userId;
        private UUID stationId;
    }
}
