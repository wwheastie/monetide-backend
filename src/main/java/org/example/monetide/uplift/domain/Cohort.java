package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class Cohort {
    private String name;
    private String description;
    private String shortDescription;
    private List<Customer> customers;
    private Integer uniqueCustomerCount;
}
