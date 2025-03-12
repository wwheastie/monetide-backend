package org.example.monetide.uplift;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CohortService {
    public Map<String, List<CustomerData>> groupCustomersByCohort(List<CustomerData> customerData) {
//        List<CustomerData> temporaryCustomerDataList = new ArrayList<>(customerData);
//
//        while (!temporaryCustomerDataList.isEmpty()) {
//
//        }
        Map<String, List<CustomerData>> cohorts = new HashMap<>();
        cohorts.put("HIGH_USAGE_LOW_PRICE", groupHighUsageLowPricedCustomersCohort(customerData));
        return cohorts;
    }

    private List<CustomerData> groupHighUsageLowPricedCustomersCohort(List<CustomerData> customerData) {
        List<CustomerData> bottomCustomersByMRR = getBottomFiftyPercentOfMRR(customerData);
        List<CustomerData> topCustomersByLogins = getTopFiftyPercentOfLogins(customerData);
        HashSet<CustomerData> hashSet = new HashSet<>(bottomCustomersByMRR);
        return topCustomersByLogins.stream()
                .filter(hashSet::contains)
                .sorted(Comparator.comparing(CustomerData::getEngagementCostRatio).reversed())
                .toList();
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
