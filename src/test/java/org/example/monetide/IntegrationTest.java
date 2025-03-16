package org.example.monetide;

import org.example.monetide.uplift.service.CohortService;
import org.example.monetide.uplift.service.CsvService;
import org.example.monetide.uplift.domain.CustomerData;
import org.example.monetide.uplift.service.EligibilityService;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IntegrationTest {
    private final CsvService csvService = new CsvService();
    private final EligibilityService eligibilityService = new EligibilityService();
    private final CohortService cohortService = new CohortService();

    @Test
    public void test() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("customer-data.csv");
        Map<String, List<CustomerData>> customerData = Optional.of(csvService.convert(inputStream))
                .map(eligibilityService::eligibleCustomers)
                .map(cohortService::groupCustomersByCohort)
                .orElseThrow();
        assertNotNull(customerData);
    }
}
