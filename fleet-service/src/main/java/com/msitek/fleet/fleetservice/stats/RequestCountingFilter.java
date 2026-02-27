package com.msitek.fleet.fleetservice.stats;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestCountingFilter extends OncePerRequestFilter {

    private final RequestCounter requestCounter;

    public RequestCountingFilter(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        String path = request.getRequestURI();
        String method = request.getMethod();
        int status = response.getStatus();

        if (!path.startsWith("/swagger-ui") && !path.startsWith("/v3/api-docs")) {
            requestCounter.increment(method, path, status);
        }
    }
}
