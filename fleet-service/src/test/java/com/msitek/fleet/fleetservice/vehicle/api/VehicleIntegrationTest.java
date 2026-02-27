package com.msitek.fleet.fleetservice.vehicle.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msitek.fleet.fleetservice.vehicle.api.dto.CreateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.api.dto.UpdateVehicleRequest;
import com.msitek.fleet.fleetservice.vehicle.domain.VehicleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleIntegrationTest {

    private static final String VEHICLES_ENDPOINT = "/vehicles";

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private static final String DEFAULT_BRAND = "Toyota";
    private static final String DEFAULT_MODEL = "Corolla";
    private static final int DEFAULT_YEAR = 2022;
    private static final VehicleStatus DEFAULT_STATUS = VehicleStatus.ACTIVE;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM vehicles");
    }

    @Test
    void shouldCreateVehicle() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "KR12345",
                vinFor(1),
                DEFAULT_BRAND,
                DEFAULT_MODEL,
                DEFAULT_YEAR,
                DEFAULT_STATUS
        );

        mockMvc.perform(post(VEHICLES_ENDPOINT)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("KR12345"))
                .andExpect(jsonPath("$.vin").value(vinFor(1)));
    }

    @Test
    void shouldReturn409WhenVinExists() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                "KR12345",
                vinFor(999),
                DEFAULT_BRAND,
                DEFAULT_MODEL,
                DEFAULT_YEAR,
                DEFAULT_STATUS
        );

        mockMvc.perform(post(VEHICLES_ENDPOINT)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post(VEHICLES_ENDPOINT)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetVehicleById() throws Exception {
        String id = createVehicleAndReturnId("KR12345", vinFor(10));

        mockMvc.perform(get(VEHICLES_ENDPOINT + "/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void shouldUpdateVehicle() throws Exception {
        String id = createVehicleAndReturnId("KR12345", vinFor(20));

        UpdateVehicleRequest update = new UpdateVehicleRequest(
                "KR99999",
                vinFor(20),
                DEFAULT_BRAND,
                DEFAULT_MODEL,
                2023,
                VehicleStatus.IN_SERVICE
        );

        mockMvc.perform(put(VEHICLES_ENDPOINT + "/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plateNumber").value("KR99999"))
                .andExpect(jsonPath("$.year").value(2023));
    }

    @Test
    void shouldDeleteVehicle() throws Exception {
        String id = createVehicleAndReturnId("KR12345", vinFor(30));

        mockMvc.perform(delete(VEHICLES_ENDPOINT + "/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(VEHICLES_ENDPOINT + "/{id}", id)
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnPagedVehicles() throws Exception {
        for (int i = 0; i < 5; i++) {
            createVehicle("KR0000" + i, vinFor(100 + i));
        }

        mockMvc.perform(get(VEHICLES_ENDPOINT + "?page=0&size=3")
                        .with(httpBasic(USERNAME, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5));
    }


    private String createVehicleAndReturnId(String plate, String vin) throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                plate,
                vin,
                DEFAULT_BRAND,
                DEFAULT_MODEL,
                DEFAULT_YEAR,
                DEFAULT_STATUS
        );

        String response = mockMvc.perform(post(VEHICLES_ENDPOINT)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        return json.get("id").asText();
    }

    private void createVehicle(String plate, String vin) throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest(
                plate,
                vin,
                DEFAULT_BRAND,
                DEFAULT_MODEL,
                DEFAULT_YEAR,
                DEFAULT_STATUS
        );

        mockMvc.perform(post(VEHICLES_ENDPOINT)
                        .with(httpBasic(USERNAME, PASSWORD))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    private static String vinFor(int n) {
        return String.format("VIN%014d", n);
    }
}