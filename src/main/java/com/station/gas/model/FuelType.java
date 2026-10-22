package com.station.gas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "fuel_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    @JsonIgnore
    private Station station;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    private Double tankCapacityLitres;
    private Double lowThresholdLitres;

    @Builder.Default
    private Boolean isActive = true;
}
