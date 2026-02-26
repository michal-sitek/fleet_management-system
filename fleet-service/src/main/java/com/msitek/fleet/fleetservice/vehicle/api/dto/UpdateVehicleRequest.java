package com.msitek.fleet.fleetservice.vehicle.api.dto;

import com.msitek.fleet.fleetservice.vehicle.domain.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateVehicleRequest(

        @NotBlank
        @Size(max = 20)
        String plateNumber,

        @NotBlank
        @Size(min = 17, max = 17)
        String vin,

        @NotBlank
        @Size(max = 100)
        String brand,

        @NotBlank
        @Size(max = 100)
        String model,

        @Min(1900)
        @Max(2100)
        int year,

        @NotNull
        VehicleStatus status
) {}