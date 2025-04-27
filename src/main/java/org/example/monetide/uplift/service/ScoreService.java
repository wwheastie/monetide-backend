package org.example.monetide.uplift.service;

import org.example.monetide.uplift.domain.Customer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class ScoreService {
    public void calculateAdoptionsScore(List<Customer> customers) {
        List<Double> allLoginMetrics = customers.stream()
                .filter(customer -> Objects.nonNull(customer.getLogins()))
                .map(customer -> Double.valueOf(customer.getLogins()))
                .toList();

        List<Double> allUsersMetrics = customers.stream()
                .filter(customer -> Objects.nonNull(customer.getUsers()))
                .map(customer -> Double.valueOf(customer.getUsers()))
                .toList();

        customers.forEach(customer -> {
            if (Objects.nonNull(customer.getLogins()) && Objects.nonNull(customer.getUsers())) {
            Double loginScore = normalize(Double.valueOf(customer.getLogins()), allLoginMetrics) * 0.6;
            Double userScore = normalize(Double.valueOf(customer.getUsers()), allUsersMetrics) * 0.4;
            Double adoptionScore = BigDecimal.valueOf(loginScore + userScore)
                    .setScale(9, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            customer.setAdoptionScore(adoptionScore);
            }
        });
    }

    public void calculateMRRScores(List<Customer> customers) {
        List<Double> allMRRScores = customers.stream()
                .filter(customer -> Objects.nonNull(customer.getMonthlyRecurringRevenue()))
                .map(Customer::getMonthlyRecurringRevenue)
                .toList();

        customers.forEach(customer -> {
            if (Objects.nonNull(customer.getMonthlyRecurringRevenue())) {
                Double mrrScore = BigDecimal.valueOf(normalize(customer.getMonthlyRecurringRevenue(), allMRRScores))
                        .setScale(10, BigDecimal.ROUND_HALF_UP)
                        .doubleValue();
                customer.setMrrScore(mrrScore);
            }
        });
    }

    public void assignBucket(List<Customer> customers) {
        customers.forEach(customer -> customer.setBucketName(determineBucket(customer)));
    }

    private String determineBucket(Customer customer) {
        Double engagementScore = customer.getAdoptionScore();
        Double valueScore = customer.getMrrScore();

        if (engagementScore >= 0.66 && valueScore >= 0.66) {
            return "Engaged High-Value";
        } else if (engagementScore >= 0.66 && valueScore >= 0.33) {
            return "Engaged Mid-Value";
        } else if (engagementScore >= 0.66 && valueScore < 0.33) {
            return "Engaged Low-Value";
        } else if (engagementScore >= 0.33 && valueScore >= 0.66) {
            return "Moderate High-Value";
        } else if (engagementScore >= 0.33 && valueScore >= 0.33) {
            return "Moderate Mid-Value";
        } else if (engagementScore >= 0.33 && valueScore < 0.33) {
            return "Moderate Low-Value";
        } else if (engagementScore < 0.33 && valueScore >= 0.66) {
            return "Disengaged High-Value";
        } else if (engagementScore < 0.33 && valueScore >= 0.33) {
            return "Disengaged Mid-Value";
        } else {
            return "Disengaged Low-Value";
        }
    }

    private Double normalize(Double value, List<Double> allValues) {
        // Step 1: Log-transform all values (+1 to each before log)
        double[] transformedValues = allValues.stream()
                .mapToDouble(v -> Math.log(v + 1))
                .toArray();

        // Step 2: Find min and max of the transformed values
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double v : transformedValues) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        // Step 3: Log-transform the input value
        double transformedInput = Math.log(value + 1);

        // Step 4: Normalize
        return (transformedInput - min) / (max - min);
    }
}
