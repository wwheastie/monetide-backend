package org.example.monetide.uplift.controller;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.monetide.uplift.domain.Customer;
import org.example.monetide.uplift.service.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class CustomerController {
    private final ScoreService scoreService;
    private final Cache<UUID, List<Customer>> customersCache;

    public CustomerController(ScoreService scoreService, Cache<UUID, List<Customer>> customersCache) {
        this.scoreService = scoreService;
        this.customersCache = customersCache;
    }

    @PostMapping("/api/v1/customer/{customerId}/customers")
    public ResponseEntity<List<Customer>> getCustomers(@PathVariable UUID customerId) {
        List<Customer> customers = customersCache.getIfPresent(customerId);
        if (customers == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        scoreService.calculateAdoptionsScore(customers);
        scoreService.calculateMRRScores(customers);
        scoreService.assignBucket(customers);
        return ResponseEntity.ok(customers);
    }
}
