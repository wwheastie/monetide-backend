package org.example.monetide;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.monetide.uplift.domain.Cohort;
import org.example.monetide.uplift.domain.CustomerData;
import org.example.monetide.uplift.service.CohortService;
import org.example.monetide.uplift.service.CsvService;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class TestUtility {
    private TestUtility() {}

    public static List<CustomerData> getCustomerData() {
        InputStream inputStream = TestUtility.class.getClassLoader().getResourceAsStream("customer-data.csv");
        CsvService csvService = new CsvService();
        return csvService.convert(inputStream);
    }

    public static List<Cohort> getCohorts(UUID id, Cache<UUID, List<Cohort>> cache) {
        CohortService cohortService = new CohortService(cache);
        return cohortService.groupCustomersByCohort(id, getCustomerData());
    }
}
