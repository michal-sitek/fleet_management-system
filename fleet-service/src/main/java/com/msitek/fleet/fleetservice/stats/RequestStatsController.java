package com.msitek.fleet.fleetservice.stats;

import com.msitek.fleet.fleetservice.stats.dto.RequestStatsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class RequestStatsController {

    private final RequestCounter requestCounter;

    public RequestStatsController(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @GetMapping("/requests")
    public RequestStatsResponse requests() {
        return new RequestStatsResponse(
                requestCounter.total(),
                requestCounter.perEndpointSnapshot(),
                requestCounter.perStatusSnapshot()
        );
    }
}
