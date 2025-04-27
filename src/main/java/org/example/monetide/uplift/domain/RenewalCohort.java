package org.example.monetide.uplift.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.YearMonth;
import java.util.List;

@Data
@Builder
public class RenewalCohort {
    @JsonIgnore
    private YearMonth yearMonth;
    private String name;
    private List<Customer> customers;
}
