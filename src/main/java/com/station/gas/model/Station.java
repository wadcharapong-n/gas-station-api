package com.station.gas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String logoUrl;
    private String tagline;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String address;
    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;

//    @Column(columnDefinition = "JSONB")
//    private String socialLinks;
//
//    @Column(columnDefinition = "JSONB")
//    private String services;
//
//    @Column(columnDefinition = "JSONB")
//    private String hours;

    @Builder.Default
    private Boolean isActive = true;

    private String apiStationId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
