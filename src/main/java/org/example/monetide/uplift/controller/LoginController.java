package org.example.monetide.uplift.controller;

import org.example.monetide.uplift.domain.Login;
import org.example.monetide.uplift.domain.LoginResponse;
import org.example.monetide.uplift.domain.SupabaseLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@RestController
public class LoginController {
    private final RestTemplate restTemplate;

    private final String apiKey;

    public LoginController(@Value("${supabase.api.key}") String apiKey) {
        this.apiKey = apiKey;
        restTemplate = new RestTemplate();
    }

    @PostMapping("/api/v1/login")
    public ResponseEntity<LoginResponse> login(@RequestBody Login login) {
        // Set up the headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", apiKey);

        // Create the request body
        HttpEntity<Login> requestEntity = new HttpEntity<>(login, headers);

        // Send the POST request to Supabase
        ResponseEntity<SupabaseLoginResponse> responseEntity = restTemplate.exchange(
                "https://bwlvbzmgfqauuqynxevi.supabase.co/auth/v1/token?grant_type=password",
                HttpMethod.POST,
                requestEntity,
                SupabaseLoginResponse.class);

        SupabaseLoginResponse supabaseLoginResponse = responseEntity.getBody();

        LoginResponse loginResponse = LoginResponse.builder()
                .token(Objects.requireNonNull(supabaseLoginResponse).getToken())
                .customerId(supabaseLoginResponse.getUser().getId())
                .build();

        return ResponseEntity.ok(loginResponse);
    }
}
