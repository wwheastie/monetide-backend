package org.example.monetide.uplift.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.example.monetide.uplift.domain.CustomerData;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class CsvService {
    public List<CustomerData> convert(final InputStream inputStream) {
        CsvToBean<CustomerData> csvToBean = new CsvToBeanBuilder<CustomerData>(new InputStreamReader(inputStream))
                .withType(CustomerData.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }
 }
