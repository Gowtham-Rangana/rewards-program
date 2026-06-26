package com.retailer.rewards.controller;

import com.retailer.rewards.dto.MonthlyReward;
import com.retailer.rewards.dto.RewardsResponse;
import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.exception.GlobalExceptionHandler;
import com.retailer.rewards.exception.InvalidRequestException;
import com.retailer.rewards.service.RewardsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RewardsControllerTest {

    @Mock
    private RewardsService rewardsService;

    @InjectMocks
    private RewardsController rewardsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rewardsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Should return 404 when customer is not found")
    void getCustomerRewards_customerNotFound_returns404() throws Exception {

        when(rewardsService.calculateRewards(999L, 3))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/v1/rewards/999")
                        .param("months", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found with ID: 999"));
    }

    @Test
    @DisplayName("Should return 400 for invalid months parameter")
    void getCustomerRewards_invalidMonths_returns400() throws Exception {

        when(rewardsService.calculateRewards(1L, 0))
                .thenThrow(new InvalidRequestException(
                        "Months parameter must be between 1 and 24. Received: 0"));

        mockMvc.perform(get("/api/v1/rewards/1")
                        .param("months", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Months parameter must be between 1 and 24. Received: 0"));
    }

    @Test
    @DisplayName("Should return 200 with custom months parameter")
    void getCustomerRewards_customMonths_returnsOk() throws Exception {

        RewardsResponse response = buildSampleResponse();
        response.setTimeFrameMonths(6);

        when(rewardsService.calculateRewards(1L, 6))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/rewards/1")
                        .param("months", "6")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timeFrameMonths").value(6));
    }

    /**
     * Helper method to build a sample RewardsResponse for testing.
     */
    private RewardsResponse buildSampleResponse() {

        RewardsResponse response = new RewardsResponse();

        response.setCustomerId(1L);
        response.setCustomerName("Alice Johnson");
        response.setEmail("alice@email.com");
        response.setTimeFrameMonths(3);
        response.setStartDate(LocalDate.now().minusMonths(3));
        response.setEndDate(LocalDate.now());
        response.setTotalRewardPoints(new BigDecimal("90.00"));
        response.setTotalAmountSpent(new BigDecimal("395.50"));
        response.setTotalTransactions(3);

        MonthlyReward monthlyReward = new MonthlyReward(
                "March 2024",
                3,
                new BigDecimal("395.50"),
                new BigDecimal("90.00"),
                Collections.emptyList());

        response.setMonthlyRewards(Collections.singletonList(monthlyReward));

        return response;
    }
}