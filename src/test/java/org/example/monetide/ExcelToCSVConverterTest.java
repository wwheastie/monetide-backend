package org.example.monetide;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.example.monetide.uplift.domain.Cohort;
import org.example.monetide.uplift.domain.CustomerData;
import org.example.monetide.uplift.service.CohortService;
import org.example.monetide.uplift.service.CsvService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;

public class ExcelToCSVConverterTest {
    @Test
    public void test() {
        // Arrange
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("customer-data.csv");
        CsvService csvService = new CsvService();
        List<CustomerData> customerDataList = csvService.convert(inputStream);
        CohortService cohortService = new CohortService();
        List<Cohort> cohorts = cohortService.groupCustomersByCohort(customerDataList);

        // Get Downloads folder (user home + Downloads)
        String downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads").toString();

        // Write each dataset to a separate CSV file
        for (Cohort cohort : cohorts) {
            String fileName = cohort.getName() + ".csv";
            File file = new File(downloadsPath, fileName);

            try (Writer writer = new FileWriter(file)) {
                StatefulBeanToCsv<CustomerData> beanToCsv = new StatefulBeanToCsvBuilder<CustomerData>(writer).build();
                beanToCsv.write(cohort.getCustomers());
                System.out.println("Written to: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
