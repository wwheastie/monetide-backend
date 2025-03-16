package org.example.monetide.uplift;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class GetCohortsResponse {
    private List<Cohort> cohorts;
}
