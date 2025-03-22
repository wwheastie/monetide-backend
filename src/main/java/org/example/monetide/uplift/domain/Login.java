package org.example.monetide.uplift.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Login {
    private String email;
    private String password;
}
