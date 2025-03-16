package org.example.monetide.uplift;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
public class CohortController {
    private final FileService fileService;
    private final CsvService csvService;
    private final EligibilityService eligibilityService;
    private final CohortService cohortService;

    public CohortController(FileService fileService, CsvService csvService, EligibilityService eligibilityService, CohortService cohortService) {
        this.fileService = fileService;
        this.csvService = csvService;
        this.eligibilityService = eligibilityService;
        this.cohortService = cohortService;
    }

    @PostMapping("/api/v1/customer/{customerId}/cohorts")
    public ResponseEntity<GetCohortsResponse> getCohorts(@PathVariable String customerId,
                                                                      @RequestParam("file") MultipartFile file) {
        InputStream inputStream = fileService.getInputStream(file);
        List<CustomerData> customerDataList = csvService.convert(inputStream);
        List<CustomerData> eligibleCustomers = eligibilityService.eligibleCustomers(customerDataList);
        List<Cohort> cohorts = cohortService.groupCustomersByCohort(eligibleCustomers);
        GetCohortsResponse response = GetCohortsResponse.builder().cohorts(cohorts).build();
        return ResponseEntity.ok(response);
    }
}
