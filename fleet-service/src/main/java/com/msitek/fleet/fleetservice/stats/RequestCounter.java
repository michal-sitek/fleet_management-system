package com.msitek.fleet.fleetservice.stats;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Component
public class RequestCounter {

    private final LongAdder total = new LongAdder();
    private final ConcurrentHashMap<String, LongAdder> perEndpoint = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, LongAdder> perStatusGroup = new ConcurrentHashMap<>();

    public void increment(String method, String path, int status) {
        total.increment();

        String endpointKey = method + " " + path;
        perEndpoint.computeIfAbsent(endpointKey, k -> new LongAdder()).increment();

        String statusGroup = (status / 100) + "xx";
        perStatusGroup.computeIfAbsent(statusGroup, k -> new LongAdder()).increment();
    }

    public long total() {
        return total.sum();
    }

    public Map<String, Long> perEndpointSnapshot() {
        return perEndpoint.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().sum()
                ));
    }

    public Map<String, Long> perStatusSnapshot() {
        return perStatusGroup.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().sum()
                ));
    }
}