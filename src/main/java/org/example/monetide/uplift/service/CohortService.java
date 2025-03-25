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
        List<Cohort> cohorts = Arrays.asList(
                groupHighUsageLowPricedCustomers(customerData),
                groupEnterpriseCustomersLegacyPricing(customerData),
                groupMidTierCustomersWithExpansionPotential(customerData),
                groupPriceSensitiveLowUsageCustomers(customerData)
        );

        calculateUniqueCustomerCount(cohorts);

        return cohorts;
    }

    private void calculateUniqueCustomerCount(List<Cohort> cohorts) {
        for (int i = 0; i < cohorts.size(); i++) {
            Cohort cohort = cohorts.get(i);
            Set<CustomerData> currentCustomerDataSet = new HashSet<>(cohort.getCustomers());

            Set<CustomerData> otherElements = new HashSet<>();
            for (int j = 0; j < cohorts.size(); j++) {
                if (i != j) {
                    otherElements.addAll(cohorts.get(j).getCustomers());
                }
            }

            Set<CustomerData> uniqueToCurrent = new HashSet<>(currentCustomerDataSet);
            uniqueToCurrent.removeAll(otherElements);

            cohort.setUniqueCustomerCount(uniqueToCurrent.size());
        }
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
