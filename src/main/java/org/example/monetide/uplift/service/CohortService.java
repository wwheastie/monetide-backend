package org.example.monetide.uplift.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.monetide.uplift.domain.Cohort;
import org.example.monetide.uplift.domain.CustomerData;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CohortService {
    private static final Integer MAX_PUPM = 200;
    private static final String ENTERPRISE_CUSTOMER = "Enterprise";
    private static final String MID_SEGMENT = "Mid";

    private final Cache<UUID, List<Cohort>> cache;

    public CohortService(@Qualifier("clientCohortsCache") Cache<UUID, List<Cohort>> cache) {
        this.cache = cache;
    }

    public List<Cohort> groupCustomersByCohort(UUID customerId, List<CustomerData> customerData) {
        List<Cohort> cohorts = new ArrayList<>();
        cohorts.add(groupHighUsageLowPricedCustomers(customerData));
        cohorts.add(groupEnterpriseCustomersLegacyPricing(customerData));
        cohorts.add(groupMidTierCustomersWithExpansionPotential(customerData));
        cohorts.add(groupPriceSensitiveLowUsageCustomers(customerData));
        cohorts.add(calculateUniqueAndDuplicateCustomers(cohorts));

        cache.put(customerId, cohorts);

        return cohorts;
    }

    private Cohort groupCustomersInMultipleCohorts(List<Cohort> cohorts) {
        return null;
    }

    private Cohort calculateUniqueAndDuplicateCustomers(List<Cohort> cohorts) {
        // Global set to store customers that appear in multiple cohorts
        Set<CustomerData> duplicateCustomersGlobal = new HashSet<>();

        for (int i = 0; i < cohorts.size(); i++) {
            Cohort cohort = cohorts.get(i);
            // Convert current cohort customers into a set to ensure uniqueness and enable set operations
            Set<CustomerData> currentCustomerDataSet = new HashSet<>(cohort.getCustomers());

            // Gather customers from all other cohorts
            Set<CustomerData> otherElements = new HashSet<>();
            for (int j = 0; j < cohorts.size(); j++) {
                if (i != j) {
                    otherElements.addAll(cohorts.get(j).getCustomers());
                }
            }

            // Compute customers that are unique to the current cohort
            Set<CustomerData> uniqueToCurrent = new HashSet<>(currentCustomerDataSet);
            uniqueToCurrent.removeAll(otherElements);

            // Compute customers that appear in both the current cohort and at least one other cohort (duplicates)
            Set<CustomerData> duplicatesInCurrent = new HashSet<>(currentCustomerDataSet);
            duplicatesInCurrent.retainAll(otherElements);

            // Optionally, if your Cohort class supports it, you could save duplicates directly on the cohort.
            // For example: cohort.setDuplicateCustomers(duplicatesInCurrent);

            // Add the duplicates from the current cohort to the global set
            duplicateCustomersGlobal.addAll(duplicatesInCurrent);

            // Update the cohort with the count of unique customers
            cohort.setUniqueCustomerCount(uniqueToCurrent.size());
        }

        // Now duplicateCustomersGlobal contains all customers found in more than one cohort.
        System.out.println("Global Duplicate Customers: " + duplicateCustomersGlobal);

        return Cohort.builder()
                .name("Customer in Multiple Cohorts")
                .description("This cohort shows all customers in that are in multiple cohorts")
                .shortDescription("Customers in more than one cohort")
                .customers(new ArrayList<>(duplicateCustomersGlobal))
                .build();
    }


    private Cohort groupMidTierCustomersWithExpansionPotential(List<CustomerData> customerData) {
        List<CustomerData> filteredCustomers = customerData.stream()
                .filter(data -> MID_SEGMENT.equals(data.getSegment()))
                .sorted(Comparator.comparing(CustomerData::getNumberOfUsers).reversed())
                .toList();


        return Cohort.builder()
                .name("Mid-Tier Users with Expansion Potential")
                .description("Push premium features before increasing base prices.")
                .shortDescription("Upgrade mid-tier user to enterprise")
                .customers(filteredCustomers.subList(0, filteredCustomers.size() / 2))
                .build();
    }

    private Cohort groupPriceSensitiveLowUsageCustomers(List<CustomerData> customerData) {
        List<CustomerData> monthlyCustomers = customerData.stream()
                .filter(data -> "Monthly".equals(data.getBillingFrequency()))
                .sorted(Comparator.comparing(CustomerData::getLogins))
                .toList();
        HashSet<CustomerData> monthlyCustomerSet = new HashSet<>(monthlyCustomers);

        List<CustomerData> bottomHalfOfLogins = getBottomHalfOfLogins(customerData);

        List<CustomerData> priceSensitiveCustomers = bottomHalfOfLogins.stream()
                .filter(monthlyCustomerSet::contains)
                .toList();

        return Cohort.builder()
                .name("Price Sensitive Low Usage")
                .description("Test messaging here with this cohort - there likely isn't a ton of value on uplifting " +
                        "them other than testing out messaging prior to sending to the more valuable customers.")
                .shortDescription("Billing Frequency = Monthly\nLogins < 50%")
                .customers(priceSensitiveCustomers)
                .build();
    }

    private List<CustomerData> getBottomHalfOfLogins(List<CustomerData> customerData) {
        int median = customerData.size() / 2;

        return customerData.stream()
                .sorted(Comparator.comparing(CustomerData::getLogins))
                .toList()
                .subList(0, median);
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
                .shortDescription("Segment = Enterprise\nMRR/Number of Users < $200")
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
                .shortDescription("MRR < 50%\nLogins > 50%")
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
