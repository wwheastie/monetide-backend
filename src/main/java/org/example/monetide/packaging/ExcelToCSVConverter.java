package org.example.monetide.packaging;

import org.apache.poi.ss.usermodel.*;
import org.example.monetide.packaging.dto.IngestResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelToCSVConverter {
    //"customer_facets", "crm_data", "product_usage", "customer_revenue"
    private static final List<String> SHEET_NAMES = List.of("crm_data", "product_usage", "customer_revenue");
    private static final int MAX_TOKENS = 500;
    private static final Map<String, List<String>> CSV_FILES = new HashMap<>();
    private static final Map<String, List<String>> DOCUMENT_IDS = new HashMap<>();
    private static StringBuilder ALL_DOCUMENT_IDS = new StringBuilder();
    private final RestTemplate restTemplate = new RestTemplate();

    public void convert(final InputStream excelFileInputStream) {
        try (Workbook workbook = WorkbookFactory.create(excelFileInputStream)) {
            for (String sheetName : SHEET_NAMES) {
                processSheet(workbook.getSheet(sheetName), sheetName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing the Excel file", e);
        }
    }

    private void processSheet(Sheet sheet, String sheetName) {
        if (sheet == null) return;

        List<String> csvData = new ArrayList<>();
        CSV_FILES.put(sheetName, csvData);
        List<String> csvDocumentIds = new ArrayList<>();
        DOCUMENT_IDS.put(sheetName, csvDocumentIds);

        Row headerRow = sheet.getRow(0);
        int maxRowsPerCsv = calculateMaxRowsPerSheet(headerRow);

        StringBuilder csvBuilder = new StringBuilder();
        appendRowToCsv(headerRow, csvBuilder);

        int currentProcessedRows = 0;
        int currentSheetCount = 1;
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            if (currentProcessedRows == maxRowsPerCsv) {
                csvData.add(csvBuilder.toString());
                uploadCsvFileToGPT(sheetName, csvDocumentIds, csvBuilder, currentSheetCount);
                currentSheetCount++;
                csvBuilder.setLength(0);
                appendRowToCsv(headerRow, csvBuilder);
                currentProcessedRows = 0;
            }

            Row row = sheet.getRow(i);
            appendRowToCsv(row, csvBuilder);
            currentProcessedRows++;
        }

        csvData.add(csvBuilder.toString());
    }

    private void appendRowToCsv(Row row, StringBuilder csvBuilder) {
        if (row == null) return;
        for (Cell cell : row) {
            csvBuilder.append(getCellValueAsString(cell)).append(",");
        }
        csvBuilder.append("\n");
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue().toString() : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }

    public Map<String, List<String>> getCsvFiles() {
        return CSV_FILES;
    }

    private int calculateMaxRowsPerSheet(Row headerRow) {
        int cellCount = headerRow.getPhysicalNumberOfCells() * 2;
        return MAX_TOKENS / cellCount;
    }

    private void uploadCsvFileToGPT(String sheetName, List<String> csvDocumentIds, StringBuilder csvBuilder, int count) {
        if (count > 5) return;
        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        String fileName = sheetName + "_" + count + ".csv";
        ByteArrayResource byteArrayResource = new ByteArrayResource(csvBytes) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", byteArrayResource);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String uploadUrl = "http://localhost:8001/v1/ingest/file";
        ResponseEntity<IngestResponse> response = restTemplate.postForEntity(uploadUrl, requestEntity, IngestResponse.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error uploading CSV file: " + response.getBody());
        }
        IngestResponse ingestResponse = response.getBody();
        csvDocumentIds.add(ingestResponse.getData().getFirst().getDocId());
        ALL_DOCUMENT_IDS.append("\"" + ingestResponse.getData().getFirst().getDocId() + "\"").append(", ");
        System.out.println("Uploaded file " + fileName);
    }

    public String getAllDocumentIds() {
        return ALL_DOCUMENT_IDS.toString();
    }
}
