package org.example.monetide.uplift.converter;

import com.opencsv.bean.AbstractBeanField;

public class IntegerConverter extends AbstractBeanField<Integer, String> {
    @Override
    protected Integer convert(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }
}
