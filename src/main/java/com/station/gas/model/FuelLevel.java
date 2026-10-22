package com.station.gas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fuel_levels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(nullable = false)
    private Double volumeLitres;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    @Builder.Default
    private Boolean overrideActive = false;

    @CreationTimestamp
    private LocalDateTime recordedAt;

    private UUID recordedBy;

    public enum Source {
        API, MANUAL
    }
}
