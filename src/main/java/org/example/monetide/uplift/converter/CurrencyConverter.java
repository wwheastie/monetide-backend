package org.example.monetide.uplift.converter;

import com.opencsv.bean.AbstractBeanField;

public class CurrencyConverter extends AbstractBeanField<Double, String> {
    @Override
    protected Double convert(String value) {
        if (value == null || value.isEmpty()) {
            return 0.0;
        }
        return Double.parseDouble(value.replaceAll("[$,]", ""));
    }
}
