package org.example.monetide.uplift.service;

import org.example.monetide.uplift.domain.Customer;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class EligibilityService {
    public List<Customer> eligibleCustomers(List<Customer> customers) {
        return customers.stream()
                .filter(this::isNotMissingCriticalData)
                .filter(this::hasNoRecentPriceChange)
                .filter(this::hasSubscriptionOverOneYear)
                .toList();
    }

    private boolean hasNoRecentPriceChange(Customer customer) {
        Double currentMonthlyRecurringRevenue = customer.getMonthlyRecurringRevenue();
        Double previousMonthlyRecurringRevenue = customer.getPreviousMonthlyRecurringRevenue();
        return currentMonthlyRecurringRevenue.equals(previousMonthlyRecurringRevenue);
    }

    private boolean hasSubscriptionOverOneYear(Customer customer) {
        Instant now = Instant.now();
        Duration duration = Duration.between(customer.getInitialSubscriptionDate(), now);
        return duration.toDays() > 365;
    }

    private boolean isNotMissingCriticalData(Customer customer) {
        if (customer.getInitialSubscriptionDate() == null) {
            System.out.println("Customer has no initial subscription date: " + customer.getAccountName());
            return false;
        }

        return true;
    }
}
