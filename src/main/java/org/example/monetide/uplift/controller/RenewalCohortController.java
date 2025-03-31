package org.example.monetide.uplift.controller;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.monetide.uplift.domain.Cohort;
import org.example.monetide.uplift.domain.CustomerData;
import org.example.monetide.uplift.domain.RenewalCohort;
import org.example.monetide.uplift.domain.RenewalCohortsResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class RenewalCohortController {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final Cache<UUID, List<Cohort>> cache;

    public RenewalCohortController(@Qualifier("clientCohortsCache") Cache<UUID, List<Cohort>> cache) {
        this.cache = cache;
    }

    @PostMapping("/api/v1/customer/{customerId}/renewals")
    public ResponseEntity<RenewalCohortsResponse> getRenewals(@PathVariable UUID customerId) {
        List<Cohort> cohorts = cache.getIfPresent(UUID.fromString(customerId.toString()));

        if (cohorts == null) {
            return ResponseEntity.badRequest().build();
        }

        Map<YearMonth, List<CustomerData>> yearMonthCustomerData = cohorts.stream()
                .flatMap(cohort -> cohort.getCustomers().stream())
                .filter(this::isGreaterThanOrEqualNoticeDate)
                .collect(Collectors.groupingBy(
                        customer -> YearMonth.from(customer.getRenewalDate()
                                .atZone(ZoneId.of("UTC")))
                        )
                );

        List<RenewalCohort> renewalCohorts = yearMonthCustomerData.entrySet()
                .stream()
                .map(this::createRenewalCohort)
                .sorted(Comparator.comparing(RenewalCohort::getYearMonth))
                .toList();

        RenewalCohortsResponse response = RenewalCohortsResponse.builder()
                .renewalCohorts(renewalCohorts)
                .build();

        return ResponseEntity.ok(response);
    }

    private boolean isGreaterThanOrEqualNoticeDate(CustomerData customerData) {
        return customerData.getRenewalDate() != null &&
                (customerData.getRenewalDate().isAfter(getDate()) || customerData.getRenewalDate().equals(getDate()));
    }

    private Instant getDate() {
        Instant date = LocalDateTime.of(2025, 4, 11, 0, 0).atZone(ZoneId.of("UTC")).toInstant();
        return date.plus(15, ChronoUnit.DAYS);
    }

    private RenewalCohort createRenewalCohort(Map.Entry<YearMonth, List<CustomerData>> entry) {
        return RenewalCohort.builder()
                .yearMonth(entry.getKey())
                .name(entry.getKey().format(FORMATTER))
                .customers(entry.getValue())
                .build();
    }
}
