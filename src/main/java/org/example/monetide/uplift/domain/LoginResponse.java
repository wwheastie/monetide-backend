package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginResponse {
    private String token;
    private String customerId;
}
