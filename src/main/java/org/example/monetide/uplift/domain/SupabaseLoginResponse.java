package org.example.monetide.uplift.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SupabaseLoginResponse {
    @JsonProperty("access_token")
    private String token;

    @JsonProperty("user")
    private SupabaseUser user;
}
