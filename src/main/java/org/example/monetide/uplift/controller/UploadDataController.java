package org.example.monetide.uplift.controller;

import com.github.benmanes.caffeine.cache.Cache;
import org.example.monetide.uplift.domain.Customer;
import org.example.monetide.uplift.service.CsvService;
import org.example.monetide.uplift.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
public class UploadDataController {
    private final FileService fileService;
    private final CsvService csvService;
    private final Cache<UUID, List<Customer>> customersCache;

    public UploadDataController(FileService fileService, CsvService csvService, Cache<UUID, List<Customer>> customersCache) {
        this.fileService = fileService;
        this.csvService = csvService;
        this.customersCache = customersCache;
    }

    @PostMapping("/api/v1/customer/{customerId}/upload")
    public ResponseEntity<List<Customer>> upload(@PathVariable UUID customerId,
                                                 @RequestParam("file") MultipartFile file) {
        InputStream inputStream = fileService.getInputStream(file);
        List<Customer> customers = csvService.convert(inputStream);
        customersCache.put(customerId, customers);
        return ResponseEntity.ok(customers);
    }
}
