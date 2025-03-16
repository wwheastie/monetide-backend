package org.example.monetide.uplift.service;

import org.example.monetide.uplift.domain.Cohort;
import org.example.monetide.uplift.domain.CustomerData;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CohortService {
    private static final Integer MAX_PUPM = 200;
    private static final String ENTERPRISE_CUSTOMER = "Enterprise";
    private static final String MID_SEGMENT = "Mid";

    public List<Cohort> groupCustomersByCohort(List<CustomerData> customerData) {
        return Arrays.asList(groupHighUsageLowPricedCustomers(customerData),
                groupEnterpriseCustomersLegacyPricing(customerData),
                groupMidTierCustomersWithExpansionPotential(customerData));
    }

    private Cohort groupMidTierCustomersWithExpansionPotential(List<CustomerData> customerData) {
        List<CustomerData> filteredCustomers = customerData.stream()
                .filter(data -> MID_SEGMENT.equals(data.getSegment()))
                .sorted(Comparator.comparing(CustomerData::getNumberOfUsers).reversed())
                .toList();


        return Cohort.builder()
                .name("Mid-Tier Users with Expansion Potential")
                .description("Push premium features before increasing base prices.")
                .customers(filteredCustomers.subList(0, filteredCustomers.size() / 2))
                .build();
    }

    private Cohort groupPriceSensitiveLowUsageCustomers(List<CustomerData> customerData) {
        return null;
    }

    private Cohort groupEnterpriseCustomersLegacyPricing(List<CustomerData> customerData) {
        List<CustomerData> filteredCustomerData = customerData.stream()
                .filter(data -> ENTERPRISE_CUSTOMER.equals(data.getSegment()))
                .filter(data -> data.getNumberOfUsers() > 0)
                .filter(data -> data.getMonthlyRecurringRevenue() / data.getNumberOfUsers() < MAX_PUPM)
                .toList();

        return Cohort.builder()
                .name("Enterprise Customer on Legacy Pricing")
                .description("Large customers are underpaying compared to new contracts, should introduce new pricing models.")
                .customers(filteredCustomerData)
                .build();
    }

    private Cohort groupHighUsageLowPricedCustomers(List<CustomerData> customerData) {
        List<CustomerData> bottomCustomersByMRR = getBottomFiftyPercentOfMRR(customerData);
        List<CustomerData> topCustomersByLogins = getTopFiftyPercentOfLogins(customerData);
        HashSet<CustomerData> hashSet = new HashSet<>(bottomCustomersByMRR);
        List<CustomerData> filteredCustomers = topCustomersByLogins.stream()
                .filter(hashSet::contains)
                .sorted(Comparator.comparing(CustomerData::getEngagementCostRatio).reversed())
                .toList();

        return Cohort.builder()
                .name("High Usage Low Priced Customers")
                .description("Customers are using significantly more than they're paying, most likely to tolerate price hikes.")
                .customers(filteredCustomers)
                .build();
    }

    private List<CustomerData> getBottomFiftyPercentOfMRR(List<CustomerData> customerData) {
        int median = customerData.size() / 2;

        return customerData.stream()
                .sorted(Comparator.comparing(CustomerData::getMonthlyRecurringRevenue))
                .toList()
                .subList(0, median);
    }

    private List<CustomerData> getTopFiftyPercentOfLogins(List<CustomerData> customerData) {
        int median = customerData.size() / 2;

        return customerData.stream()
                .sorted(Comparator.comparing(CustomerData::getLogins).reversed())
                .toList()
                .subList(0, median);
    }
}
