package org.example.monetide.uplift.converter;

import com.opencsv.bean.AbstractBeanField;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter extends AbstractBeanField<Instant, String> {
    @Override
    protected Instant convert(String value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
        LocalDate localDate = LocalDate.parse(value, formatter);
        return localDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant();
    }

    private String getDateFormat(String value) {
        int count = StringUtils.substringBefore(value, "/").length();
        return count == 1 ? "M/dd/yy" : "MM/dd/yy";
    }
}
