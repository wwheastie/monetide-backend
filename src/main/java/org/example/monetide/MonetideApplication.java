package org.example.monetide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonetideApplication {

    // Service that reads the data from the csv spreadsheet & converts into domain objects
    // Service to only keep eligible customers to uplift price
    // Service to group items in a list to a sublist

    public static void main(String[] args) {
        SpringApplication.run(MonetideApplication.class, args);
    }

}
