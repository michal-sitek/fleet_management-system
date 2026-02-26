package com.msitek.fleet.fleetservice.vehicle.api;

import com.msitek.fleet.fleetservice.vehicle.api.dto.CreateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.PageResponse;
import com.msitek.fleet.fleetservice.vehicle.api.dto.UpdateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.VehicleResponse;
import com.msitek.fleet.fleetservice.vehicle.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public VehicleResponse create(@Valid @RequestBody CreateVehicleRequest request) {
        return vehicleService.create(request);
    }

    @GetMapping
    public PageResponse<VehicleResponse> list(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return vehicleService.list(q, pageable);
    }

    @GetMapping("/{id}")
    public VehicleResponse getById(@PathVariable UUID id) {
        return vehicleService.getById(id);
    }

    @PutMapping("/{id}")
    public VehicleResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateVehicleRequest request
    ) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        vehicleService.delete(id);
    }

    @GetMapping("/by-vin/{vin}")
    public VehicleResponse getByVin(@PathVariable String vin) {
        return vehicleService.getByVin(vin);
    }

    @GetMapping("/by-plate/{plateNumber}")
    public VehicleResponse getByPlate(@PathVariable String plateNumber) {
        return vehicleService.getByPlateNumber(plateNumber);
    }
}
