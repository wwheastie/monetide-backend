package org.example.monetide.uplift.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.*;
import org.example.monetide.uplift.converter.CurrencyConverter;
import org.example.monetide.uplift.converter.DateConverter;
import org.example.monetide.uplift.converter.IntegerConverter;
import org.example.monetide.uplift.serializer.InstantToShortDateSerializer;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @CsvBindByName(column = "Account Name")
    @JsonProperty("Account Name")
    private String accountName;

    @CsvCustomBindByName(column = "Contract Monthly Recurring Revenue (converted)", converter = CurrencyConverter.class)
    @JsonProperty("Monthly Recurring Revenue")
    private Double monthlyRecurringRevenue;

    @CsvBindByName(column = "Segment")
    @JsonProperty("Segment")
    private String segment;

    @CsvBindByName(column = "Renewal Manager")
    @JsonProperty("Renewal Manager")
    private String renewalManager;

    @CsvBindByName(column = "Renewal Team")
    @JsonProperty("Renewal Team")
    private String renewalTeam;

    @CsvCustomBindByName(column = "Managed Renewal Date", converter = DateConverter.class)
    @JsonProperty("Managed Renewal Date")
    private Instant managedRenewalDate;

    @CsvBindByName(column = "Region")
    @JsonProperty("Region")
    private String region;

    @Setter
    @JsonProperty("Adoption Score")
    private Double adoptionScore;

    @Setter
    @JsonProperty("MRR Score")
    private Double mrrScore;

    @Setter
    @JsonProperty("Bucket Name")
    private String bucketName;

    // Usage Metrics
    @CsvCustomBindByName(column = "Total Logins (90-Days)", converter = IntegerConverter.class)
    private Integer logins;

    @CsvCustomBindByName(column = "# of Users", converter = IntegerConverter.class)
    private Integer users;

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

    @JsonIgnore
    public Double getEngagementCostRatio() {
        if (monthlyRecurringRevenue == 0) {
            return Double.MAX_VALUE;
        }
        return logins / monthlyRecurringRevenue;
    }
}
