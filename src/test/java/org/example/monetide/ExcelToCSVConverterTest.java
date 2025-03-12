package org.example.monetide;

import org.example.monetide.packaging.ExcelToCSVConverter;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExcelToCSVConverterTest {


    @Test
    public void test() {
        // Arrange
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample.xlsx");
        ExcelToCSVConverter converter = new ExcelToCSVConverter();

        // ACT
        converter.convert(inputStream);

        // Assert
        Map<String, List<String>> csvFiles = converter.getCsvFiles();
        assertNotNull(converter.getAllDocumentIds());
//        assertEquals(4, csvFiles.size());
        assertNotNull(csvFiles);
    }
}
