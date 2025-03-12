package org.example.monetide.packaging;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class AmazonS3Config {
    @Bean
    public S3Client amazonS3Client() {
        // Set up credentials
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                "6156fb994865f3d4d08206ffd2ed360a",
                "d620a2eaf723bf0c036bd66fa4849ca9acc18305ed9efbb959d8c12e1d6bf38a"
        );

        // Configure S3 client to use the R2 endpoint
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.US_EAST_1) // Use any region (R2 ignores this, but it's required by the SDK)
                .endpointOverride(URI.create("https://b63892e4e04ad5158b6a1d8483663bb9.r2.cloudflarestorage.com")) // Replace with your R2 endpoint
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
