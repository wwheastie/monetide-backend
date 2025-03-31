package org.example.monetide.uplift.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.monetide.uplift.domain.Cohort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean("clientCohortsCache")
    public Cache<UUID, List<Cohort>> clientCohortsCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(14, TimeUnit.DAYS)
                .maximumSize(100)
                .build();
    }
}
