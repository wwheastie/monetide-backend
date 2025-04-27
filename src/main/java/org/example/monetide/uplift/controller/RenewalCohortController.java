//package org.example.monetide.uplift.controller;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import org.example.monetide.uplift.domain.*;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.Instant;
//import java.time.YearMonth;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@RestController
//public class RenewalCohortController {
//    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
//
//    private final Cache<UUID, List<Cohort>> cache;
//
//    public RenewalCohortController(@Qualifier("clientCohortsCache") Cache<UUID, List<Cohort>> cache) {
//        this.cache = cache;
//    }
//
//    @PostMapping("/api/v1/customer/{customerId}/renewals")
//    public ResponseEntity<RenewalCohortsResponse> getRenewals(@PathVariable UUID customerId, @RequestBody RenewalCohortsRequest request) {
//        List<Cohort> cohorts = cache.getIfPresent(UUID.fromString(customerId.toString()));
//
//        if (cohorts == null) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Map<YearMonth, List<Customer>> yearMonthCustomerData = cohorts.stream()
//                .flatMap(cohort -> cohort.getCustomers().stream())
//                .filter(customerData -> isGreaterThanOrEqualNoticeDate(customerData, request))
//                .collect(Collectors.groupingBy(
//                        customer -> YearMonth.from(customer.getRenewalDate()
//                                .atZone(ZoneId.of("UTC")))
//                        )
//                );
//
//        List<RenewalCohort> renewalCohorts = yearMonthCustomerData.entrySet()
//                .stream()
//                .map(this::createRenewalCohort)
//                .sorted(Comparator.comparing(RenewalCohort::getYearMonth))
//                .toList();
//
//        RenewalCohortsResponse response = RenewalCohortsResponse.builder()
//                .renewalCohorts(renewalCohorts)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    private boolean isGreaterThanOrEqualNoticeDate(Customer customer, RenewalCohortsRequest request) {
//        Instant instateDate = request.getNoticeSentDate().atStartOfDay(ZoneId.of("UTC")).toInstant();
//        Instant date = instateDate.plus(request.getDaysOfNotice(), ChronoUnit.DAYS);
//
//        return customer.getRenewalDate() != null &&
//                (customer.getRenewalDate().isAfter(date) || customer.getRenewalDate().equals(date));
//    }
//
//    private RenewalCohort createRenewalCohort(Map.Entry<YearMonth, List<Customer>> entry) {
//        return RenewalCohort.builder()
//                .yearMonth(entry.getKey())
//                .name(entry.getKey().format(FORMATTER))
//                .customers(entry.getValue())
//                .build();
//    }
//}
