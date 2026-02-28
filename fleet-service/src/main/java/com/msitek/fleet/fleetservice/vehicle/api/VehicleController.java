package com.msitek.fleet.fleetservice.vehicle.api;

import com.msitek.fleet.fleetservice.vehicle.api.dto.CreateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.PageResponse;
import com.msitek.fleet.fleetservice.vehicle.api.dto.UpdateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.VehicleResponse;
import com.msitek.fleet.fleetservice.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Operations related to fleet vehicle management")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(summary = "Create new vehicle", description = "Adds a new vehicle to the fleet")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Vehicle with given VIN already exists", content = @Content)
    })
    @PostMapping
    public VehicleResponse create(@Valid @RequestBody CreateVehicleRequest request) {
        return vehicleService.create(request);
    }

    @Operation(summary = "List vehicles", description = "Returns paginated list of vehicles with optional search query")
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @GetMapping
    public PageResponse<VehicleResponse> list(
            @Parameter(description = "Search query (plate number, VIN, brand, model)")
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return vehicleService.list(q, pageable);
    }

    @Operation(summary = "Get vehicle by ID", description = "Returns vehicle details by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @GetMapping("/{id}")
    public VehicleResponse getById(
            @Parameter(description = "Vehicle UUID", required = true)
            @PathVariable UUID id) {
        return vehicleService.getById(id);
    }

    @Operation(summary = "Update vehicle", description = "Updates vehicle data by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @PutMapping("/{id}")
    public VehicleResponse update(
            @Parameter(description = "Vehicle UUID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        return vehicleService.update(id, request);
    }

    @Operation(summary = "Delete vehicle", description = "Removes vehicle from the fleet")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Vehicle UUID", required = true)
            @PathVariable UUID id) {
        vehicleService.delete(id);
    }

    @Operation(summary = "Get vehicle by VIN", description = "Returns vehicle details by VIN number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @GetMapping("/by-vin/{vin}")
    public VehicleResponse getByVin(
            @Parameter(description = "Vehicle VIN (17 characters)", required = true)
            @PathVariable String vin) {
        return vehicleService.getByVin(vin);
    }

    @Operation(summary = "Get vehicle by plate number", description = "Returns vehicle details by plate number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle found"),
            @ApiResponse(responseCode = "404", description = "Vehicle not found", content = @Content)
    })
    @GetMapping("/by-plate/{plateNumber}")
    public VehicleResponse getByPlate(
            @Parameter(description = "Vehicle plate number", required = true)
            @PathVariable String plateNumber) {
        return vehicleService.getByPlateNumber(plateNumber);
    }
}
