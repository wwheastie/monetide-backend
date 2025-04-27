package org.example.monetide.uplift.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.example.monetide.uplift.domain.Customer;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsvService {
    public List<Customer> convert(final InputStream inputStream) {
        // 1. Read the InputStream into a cleaned string
        String cleanedCsv = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .map(this::cleanCsvLine) // Clean each line
                .collect(Collectors.joining("\n"));

        // 2. Convert the cleaned string back into an InputStream
        InputStream cleanedInputStream = new ByteArrayInputStream(cleanedCsv.getBytes());

        // 3. Pass the cleaned stream to CsvToBean
        CsvToBean<Customer> csvToBean = new CsvToBeanBuilder<Customer>(new InputStreamReader(cleanedInputStream))
                .withType(Customer.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();

    }

    private String cleanCsvLine(String line) {
        // Clean ONLY the data rows, not the header
        if (line.startsWith("Account Name")) {
            return line; // Don't touch header
        }

        // Remove $ signs
        line = line.replace("$", "");

        // Replace commas inside quotes (likely inside numbers) with nothing
        // Note: this is a simple approach assuming no nested quotes
        line = line.replaceAll("\"(\\d+),(\\d+\\.\\d{2})\"", "\"$1$2\"");

        return line;
    }
 }
