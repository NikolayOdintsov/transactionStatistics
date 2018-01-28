package com.example.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TransactionStatisticsApplication {
    private static final Logger logger = LoggerFactory.getLogger(TransactionStatisticsApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TransactionStatisticsApplication.class, args);

        logger.debug("Application started");
    }
}
