package com.msitek.fleet.fleetservice.vehicle.api.dto;

import com.msitek.fleet.fleetservice.vehicle.domain.VehicleStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VehicleResponse(
        UUID id,
        String plateNumber,
        String vin,
        String brand,
        String model,
        int year,
        VehicleStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
