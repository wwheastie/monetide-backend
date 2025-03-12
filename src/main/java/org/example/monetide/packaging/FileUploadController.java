package org.example.monetide.packaging;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Set;

@RestController()
public class FileUploadController {
    private static final Set<String> ACCEPTABLE_FILE_TYPES = Set.of("csv", "xlsx");
    private final S3Client s3;

    public FileUploadController(S3Client s3) {
        this.s3 = s3;
    }

    @PostMapping("/api/v1/customer/{customerId}/segment/file")
    public ResponseEntity<?> uploadSegmentFile(@RequestParam("file") MultipartFile file,
                                            @PathVariable String customerId) {
        if (!ACCEPTABLE_FILE_TYPES.contains(getFileExtension(file))) {
            return ResponseEntity.badRequest().body("File type not accepted");
        }

        uploadFile(customerId, file);

        return ResponseEntity.ok().body("File uploaded successfully");
    }

    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void uploadFile(String customerId, MultipartFile file) {
        // Example: Upload an object
        String bucketName = "test-bucket";
        String objectKey = customerId + "/" + file.getOriginalFilename();

        try {
            // Upload file to R2
            s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getInputStream().available()));
            System.out.println("File uploaded successfully!");

            // List objects in the bucket
            s3.listObjectsV2(builder -> builder.bucket(bucketName))
                    .contents()
                    .forEach(object -> System.out.println("Found object: " + object.key()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
