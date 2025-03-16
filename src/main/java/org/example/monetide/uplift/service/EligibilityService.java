package org.example.monetide.uplift.service;

import org.example.monetide.uplift.domain.CustomerData;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class EligibilityService {
    public List<CustomerData> eligibleCustomers(List<CustomerData> customers) {
        return customers.stream()
                .filter(this::hasNoRecentPriceChange)
                .filter(this::hasSubscriptionOverOneYear)
                .toList();
    }

    private boolean hasNoRecentPriceChange(CustomerData customerData) {
        Double currentMonthlyRecurringRevenue = customerData.getMonthlyRecurringRevenue();
        Double previousMonthlyRecurringRevenue = customerData.getPreviousMonthlyRecurringRevenue();
        return currentMonthlyRecurringRevenue.equals(previousMonthlyRecurringRevenue);
    }

    private boolean hasSubscriptionOverOneYear(CustomerData customerData) {
        Instant now = Instant.now();
        Duration duration = Duration.between(customerData.getInitialSubscriptionDate(), now);
        return duration.toDays() > 365;
    }
}
