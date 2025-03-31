package org.example.monetide;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.monetide.uplift.controller.RenewalCohortController;
import org.example.monetide.uplift.domain.Cohort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RenewalControllerTest {
    private final Cache<UUID, List<Cohort>> cache = Caffeine.newBuilder()
            .expireAfterWrite(14, TimeUnit.DAYS)
            .maximumSize(100)
            .build();

    private final RenewalCohortController controller = new RenewalCohortController(cache);

    @Test
    public void test() {
        UUID clientId = UUID.randomUUID();
        TestUtility.getCohorts(clientId, cache);
        controller.getRenewals(clientId);
    }
}
