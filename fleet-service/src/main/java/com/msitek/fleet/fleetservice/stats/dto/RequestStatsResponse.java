package com.msitek.fleet.fleetservice.stats.dto;

import java.util.Map;

public record RequestStatsResponse(
        long totalRequests,
        Map<String, Long> perEndpoint,
        Map<String, Long> perStatus
) {
}
