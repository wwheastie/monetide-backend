package org.example.monetide.uplift.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;
import org.example.monetide.uplift.converter.CurrencyConverter;
import org.example.monetide.uplift.converter.DateConverter;
import org.example.monetide.uplift.converter.IntegerConverter;
import org.example.monetide.uplift.serializer.InstantToShortDateSerializer;

import java.time.Instant;

@Getter
public class CustomerData {
    @CsvBindByName(column = "Account Name")
    @JsonProperty("Customer")
    private String accountName;

    @CsvCustomBindByName(column = "Contract Monthly Recurring Revenue (converted)", converter = CurrencyConverter.class)
    @JsonProperty("MRR")
    private Double monthlyRecurringRevenue;

    @CsvBindByName(column = "Segment")
    @JsonProperty("Plan")
    private String segment;

    @CsvCustomBindByName(column = "First Subscription Date", converter = DateConverter.class)
    @JsonProperty("Initial Subscription")
    @JsonSerialize(using = InstantToShortDateSerializer.class)
    private Instant initialSubscriptionDate;

    @CsvCustomBindByName(column = "Previous Months Value (converted)", converter = CurrencyConverter.class)
    @JsonIgnore
    private Double previousMonthlyRecurringRevenue;

    @CsvBindByName(column = "Billing Frequency")
    @JsonIgnore
    private String billingFrequency;

    @CsvCustomBindByName(column = "Total Logins (90-Days)", converter = IntegerConverter.class)
    @JsonIgnore
    private Integer logins;

    @CsvCustomBindByName(column = "# of Users", converter = IntegerConverter.class)
    @JsonIgnore
    private Integer numberOfUsers;

    @CsvCustomBindByName(column = "Managed Renewal Date", converter = DateConverter.class)
    @JsonIgnore
    private Instant renewalDate;

    @JsonIgnore
    public Double getEngagementCostRatio() {
        if (monthlyRecurringRevenue == 0) {
            return Double.MAX_VALUE;
        }
        return logins / monthlyRecurringRevenue;
    }

//    private String cohortName;
}
