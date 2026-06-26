package com.retailer.rewards.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RewardsCalculatorTest {

    private RewardsCalculator rewardsCalculator;

    @BeforeEach
    void setUp() {
        rewardsCalculator = new RewardsCalculator();
    }

    @ParameterizedTest
    @CsvSource({
            "0.00, 0",
            "25.00, 0",
            "49.99, 0",
            "50.00, 0",
            "51.00, 1.00",
            "75.00, 25.00",
            "75.50, 25.50",
            "99.99, 49.99",
            "100.00, 50.00",
            "101.00, 52.00",
            "120.00, 90.00",
            "120.75, 91.50",
            "150.00, 150.00",
            "200.00, 250.00",
            "500.00, 850.00"
    })
    @DisplayName("Should calculate correct points for various purchase amounts")
    void calculatePoints_variousAmounts(String amount, String expectedPoints) {

        BigDecimal result = rewardsCalculator.calculatePoints(new BigDecimal(amount));

        assertEquals(
                new BigDecimal(expectedPoints).stripTrailingZeros(),
                result.stripTrailingZeros());
    }

    @Test
    @DisplayName("Should return zero points for negative purchase amount")
    void calculatePoints_negativeAmount_returnsZero() {

        BigDecimal result = rewardsCalculator.calculatePoints(new BigDecimal("-25.00"));

        assertEquals(
                BigDecimal.ZERO.stripTrailingZeros(),
                result.stripTrailingZeros());
    }
}