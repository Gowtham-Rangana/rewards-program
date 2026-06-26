package com.retailer.rewards.controller;

import com.retailer.rewards.dto.RewardsResponse;
import com.retailer.rewards.service.RewardsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the rewards calculation endpoint.
 * Provides reward points information for customers based on their purchase history.
 */
@RestController
@RequestMapping("/api/v1/rewards")
public class RewardsController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);
    private static final int DEFAULT_MONTHS = 3;

    private final RewardsService rewardsService;

    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    /**
     * Calculates and returns reward points for a specific customer.
     * * @param customerId the unique identifier of the customer
     * @param months     the number of months to look back (default: 3, max: 24)
     * @return the rewards response with monthly breakdown and totals
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<RewardsResponse> getCustomerRewards(
            @PathVariable Long customerId,
            @RequestParam(value = "months", defaultValue = "3") int months) {

        logger.info("Received rewards request for customer ID: {} with months: {}", customerId, months);

        RewardsResponse response = rewardsService.calculateRewards(customerId, months);

        return ResponseEntity.ok(response);
    }
}