package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CohortsResponse {
    private List<Cohort> cohorts;
}
