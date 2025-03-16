package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Cohort {
    private String name;
    private String description;
    private List<CustomerData> customers;
}
