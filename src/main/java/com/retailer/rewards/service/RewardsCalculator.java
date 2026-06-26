package com.retailer.rewards.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Utility component responsible for calculating reward points based on transaction amount.
 * * <p>Reward Rules:</p>
 * <ul>
 * <li>2 points for every dollar spent over $100</li>
 * <li>1 point for every dollar spent between $50 and $100</li>
 * <li>0 points for amounts $50 or below</li>
 * </ul>
 */
@Component
public class RewardsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(RewardsCalculator.class);

    private static final BigDecimal TIER_ONE_THRESHOLD = BigDecimal.valueOf(50);
    private static final BigDecimal TIER_TWO_THRESHOLD = BigDecimal.valueOf(100);
    private static final BigDecimal TIER_TWO_MULTIPLIER = BigDecimal.valueOf(2);

    public BigDecimal calculatePoints(BigDecimal purchaseAmount) {

        BigDecimal points = purchaseAmount.subtract(TIER_TWO_THRESHOLD)
                .max(BigDecimal.ZERO)
                .multiply(TIER_TWO_MULTIPLIER)
                .add(purchaseAmount.min(TIER_TWO_THRESHOLD)
                        .subtract(TIER_ONE_THRESHOLD)
                        .max(BigDecimal.ZERO));

        logger.debug("Points for ${}: {}", purchaseAmount, points);
        return points;
    }
}
