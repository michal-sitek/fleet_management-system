package com.msitek.fleet.fleetservice.vehicle.service;

import com.msitek.fleet.fleetservice.vehicle.api.dto.CreateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.PageResponse;
import com.msitek.fleet.fleetservice.vehicle.api.dto.UpdateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.VehicleResponse;
import com.msitek.fleet.fleetservice.vehicle.domain.Vehicle;
import com.msitek.fleet.fleetservice.vehicle.exception.VehicleConflictException;
import com.msitek.fleet.fleetservice.vehicle.exception.VehicleNotFoundException;
import com.msitek.fleet.fleetservice.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleResponse create(CreateVehicleRequest request) {

        if (vehicleRepository.existsByVin(request.vin())) {
            throw new VehicleConflictException("VIN already exists: " + request.vin());
        }

        if (vehicleRepository.existsByPlateNumber(request.plateNumber())) {
            throw new VehicleConflictException("Plate number already exists: " + request.plateNumber());
        }

        LocalDateTime now = LocalDateTime.now();

        Vehicle vehicle = Vehicle.builder()
                .id(UUID.randomUUID())
                .plateNumber(request.plateNumber())
                .vin(request.vin())
                .brand(request.brand())
                .model(request.model())
                .year(request.year())
                .status(request.status())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);

        return new VehicleResponse(
                saved.getId(),
                saved.getPlateNumber(),
                saved.getVin(),
                saved.getBrand(),
                saved.getModel(),
                saved.getYear(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public PageResponse<VehicleResponse> list(String q, Pageable pageable) {

        Page<Vehicle> page;

        if (q == null || q.isBlank()) {
            page = vehicleRepository.findAll(pageable);
        } else {
            page = vehicleRepository.search(q.trim().toLowerCase(), pageable);
        }

        Function<Vehicle, VehicleResponse> mapper = v -> new VehicleResponse(
                v.getId(),
                v.getPlateNumber(),
                v.getVin(),
                v.getBrand(),
                v.getModel(),
                v.getYear(),
                v.getStatus(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );

        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public VehicleResponse getById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found: " + id));

        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getVin(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }

    public VehicleResponse update(UUID id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found: " + id));

        if (vehicleRepository.existsByVinAndIdNot(request.vin(), id)) {
            throw new VehicleConflictException("VIN already exists: " + request.vin());
        }
        if (vehicleRepository.existsByPlateNumberAndIdNot(request.plateNumber(), id)) {
            throw new VehicleConflictException("Plate number already exists: " + request.plateNumber());
        }

        vehicle.setPlateNumber(request.plateNumber());
        vehicle.setVin(request.vin());
        vehicle.setBrand(request.brand());
        vehicle.setModel(request.model());
        vehicle.setYear(request.year());
        vehicle.setStatus(request.status());
        vehicle.setUpdatedAt(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicle);

        return new VehicleResponse(
                saved.getId(),
                saved.getPlateNumber(),
                saved.getVin(),
                saved.getBrand(),
                saved.getModel(),
                saved.getYear(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    public void delete(UUID id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException("Vehicle not found: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleResponse getByVin(String vin) {
        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> new VehicleNotFoundException("VIN: " + vin));

        return mapToResponse(vehicle);
    }

    public VehicleResponse getByPlateNumber(String plateNumber) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new VehicleNotFoundException("Plate number: " + plateNumber));

        return mapToResponse(vehicle);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getVin(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getStatus(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}
