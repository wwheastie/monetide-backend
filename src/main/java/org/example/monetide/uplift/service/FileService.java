package org.example.monetide.uplift.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Service
public class FileService {
    private static final Set<String> ACCEPTABLE_FILE_TYPES = Set.of("csv", "xlsx");

    public void isValidFile(MultipartFile file) {
        if (!ACCEPTABLE_FILE_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type: " + file.getContentType());
        }
    }

    public InputStream getInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
