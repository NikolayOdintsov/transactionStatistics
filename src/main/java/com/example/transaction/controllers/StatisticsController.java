package com.example.transaction.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by nikolay.odintsov on 27.01.18.
 */

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @GetMapping(produces = "application/json")
    public ResponseEntity<String> getStatistics() {
        logger.debug("GET statistics called");

        // TODO: impl

        return ResponseEntity.ok("");
    }
}
