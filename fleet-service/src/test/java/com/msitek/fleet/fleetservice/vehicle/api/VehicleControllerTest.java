package com.msitek.fleet.fleetservice.vehicle.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msitek.fleet.fleetservice.common.error.GlobalExceptionHandler;
import com.msitek.fleet.fleetservice.config.SecurityConfig;
import com.msitek.fleet.fleetservice.vehicle.api.dto.CreateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.PageResponse;
import com.msitek.fleet.fleetservice.vehicle.api.dto.UpdateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.VehicleResponse;
import com.msitek.fleet.fleetservice.vehicle.domain.VehicleStatus;
import com.msitek.fleet.fleetservice.vehicle.exception.VehicleConflictException;
import com.msitek.fleet.fleetservice.vehicle.exception.VehicleNotFoundException;
import com.msitek.fleet.fleetservice.vehicle.service.VehicleService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@Import({GlobalExceptionHandler.class, SecurityConfig.class})
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private com.msitek.fleet.fleetservice.stats.RequestCounter requestCounter;

    @Test
    void shouldReturn401WhenUnauthorized() throws Exception {
        CreateVehicleRequest req = new CreateVehicleRequest(
                "KR12345",
                "12345678901234567",
                "Toyota",
                "Corolla",
                2022,
                VehicleStatus.ACTIVE
        );

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateVehicle() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Mockito.when(vehicleService.create(any(CreateVehicleRequest.class)))
                .thenReturn(new VehicleResponse(
                        id,
                        "KR12345",
                        "12345678901234567",
                        "Toyota",
                        "Corolla",
                        2022,
                        VehicleStatus.ACTIVE,
                        now,
                        now
                ));

        CreateVehicleRequest req = new CreateVehicleRequest(
                "KR12345",
                "12345678901234567",
                "Toyota",
                "Corolla",
                2022,
                VehicleStatus.ACTIVE
        );

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.vin").value("12345678901234567"))
                .andExpect(jsonPath("$.plateNumber").value("KR12345"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn400ForInvalidCreateRequest() throws Exception {
        String invalidJson = """
            {
              "plateNumber": "",
              "vin": "short",
              "brand": "",
              "model": "",
              "year": 1800,
              "status": null
            }
            """;

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.fieldErrors").isArray());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn404WhenVehicleNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(vehicleService.getById(eq(id)))
                .thenThrow(new VehicleNotFoundException("Vehicle not found: " + id));

        mockMvc.perform(get("/vehicles/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn409OnConflict() throws Exception {
        Mockito.when(vehicleService.create(any(CreateVehicleRequest.class)))
                .thenThrow(new VehicleConflictException("VIN already exists: 12345678901234567"));

        CreateVehicleRequest req = new CreateVehicleRequest(
                "KR12345",
                "12345678901234567",
                "Toyota",
                "Corolla",
                2022,
                VehicleStatus.ACTIVE
        );

        mockMvc.perform(post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldListVehiclesWithPagination() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        PageResponse<VehicleResponse> response = new PageResponse<>(
                List.of(new VehicleResponse(
                        id,
                        "KR12345",
                        "12345678901234567",
                        "Toyota",
                        "Corolla",
                        2022,
                        VehicleStatus.ACTIVE,
                        now,
                        now
                )),
                0,
                20,
                1,
                1
        );

        Mockito.when(vehicleService.list(anyString(), any(Pageable.class))).thenReturn(response);

        mockMvc.perform(get("/vehicles")
                        .param("q", "")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldUpdateVehicle() throws Exception {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Mockito.when(vehicleService.update(eq(id), any(UpdateVehicleRequest.class)))
                .thenReturn(new VehicleResponse(
                        id,
                        "KR99999",
                        "12345678901234567",
                        "Toyota",
                        "Corolla",
                        2023,
                        VehicleStatus.IN_SERVICE,
                        now.minusDays(1),
                        now
                ));

        UpdateVehicleRequest req = new UpdateVehicleRequest(
                "KR99999",
                "12345678901234567",
                "Toyota",
                "Corolla",
                2023,
                VehicleStatus.IN_SERVICE
        );

        mockMvc.perform(put("/vehicles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("KR99999"))
                .andExpect(jsonPath("$.year").value(2023))
                .andExpect(jsonPath("$.status").value("IN_SERVICE"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/vehicles/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}