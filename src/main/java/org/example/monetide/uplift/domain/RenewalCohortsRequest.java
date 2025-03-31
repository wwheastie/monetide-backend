package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class RenewalCohortsRequest {
    private LocalDate noticeSentDate;
    private Integer daysOfNotice;
}
