package org.example.monetide;

import org.example.monetide.uplift.domain.Customer;
import org.example.monetide.uplift.service.CsvService;
import org.example.monetide.uplift.service.ScoreService;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ScoreServiceTest {
    @Test
    public void testAdoptionScore() {
        List<Customer> customers = getCustomers();
        ScoreService scoreService = new ScoreService();
        scoreService.calculateAdoptionsScore(customers);
        Customer customer = customers.getFirst();
        assertNotNull(customer);
        assertEquals(0.488113186, customer.getAdoptionScore());
    }

    @Test
    public void testMRRScore() {
        List<Customer> customers = getCustomers();
        ScoreService scoreService = new ScoreService();
        scoreService.calculateMRRScores(customers);
        Customer customer = customers.getFirst();
        assertNotNull(customer);
        assertEquals(1, customer.getMrrScore());

        customer = customers.get(1);
        assertNotNull(customer);
        assertEquals(0.9312214962, customer.getMrrScore());
    }

    private List<Customer> getCustomers() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("customer-data.csv");
        CsvService csvService = new CsvService();
        return csvService.convert(inputStream);
    }
}
