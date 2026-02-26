package com.msitek.fleet.fleetservice.vehicle.repository;

import com.msitek.fleet.fleetservice.vehicle.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    boolean existsByVin(String vin);
    boolean existsByPlateNumber(String plateNumber);

    @Query("""
       select v from Vehicle v
       where lower(v.plateNumber) like concat('%', :q, '%')
          or lower(v.vin) like concat('%', :q, '%')
          or lower(v.brand) like concat('%', :q, '%')
          or lower(v.model) like concat('%', :q, '%')
       """)
    Page<Vehicle> search(@Param("q") String q, Pageable pageable);

    boolean existsByVinAndIdNot(String vin, java.util.UUID id);
    boolean existsByPlateNumberAndIdNot(String plateNumber, java.util.UUID id);

    Optional<Vehicle> findByVin(String vin);
    Optional<Vehicle> findByPlateNumber(String plateNumber);
}
