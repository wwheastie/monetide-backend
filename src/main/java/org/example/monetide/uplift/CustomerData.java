package org.example.monetide.uplift;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;

import java.time.Instant;

@Getter
public class CustomerData {
    @CsvCustomBindByName(column = "Contract Monthly Recurring Revenue (converted)", converter = CurrencyConverter.class)
    private Double monthlyRecurringRevenue;

    @CsvCustomBindByName(column = "Previous Months Value (converted)", converter = CurrencyConverter.class)
    private Double previousMonthlyRecurringRevenue;

    @CsvCustomBindByName(column = "First Subscription Date", converter = DateConverter.class)
    private Instant initialSubscriptionDate;

    @CsvCustomBindByName(column = "Total Logins (90-Days)", converter = IntegerConverter.class)
    private Integer logins;

    @CsvBindByName(column = "Account Name")
    private String accountName;

    public Double getEngagementCostRatio() {
        if (monthlyRecurringRevenue == 0) {
            return Double.MAX_VALUE;
        }
        return logins / monthlyRecurringRevenue;
    }
}
