package com.msitek.fleet.fleetservice.stats;

import com.msitek.fleet.fleetservice.stats.dto.RequestStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
@Tag(name = "Statistics", description = "Application request statistics")
public class RequestStatsController {

    private final RequestCounter requestCounter;

    public RequestStatsController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Operation(
            summary = "Get request statistics",
            description = "Returns total number of executed requests, grouped by endpoint and status code category"
    )
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @GetMapping("/requests")
    public RequestStatsResponse requests() {
        return new RequestStatsResponse(
                requestCounter.total(),
                requestCounter.perEndpointSnapshot(),
                requestCounter.perStatusSnapshot()
        );
    }
}
