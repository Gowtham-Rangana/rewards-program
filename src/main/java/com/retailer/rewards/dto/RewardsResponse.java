package com.retailer.rewards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO representing the complete rewards response for a customer,
 * including customer details, monthly breakdown, and totals.
 */
public class RewardsResponse {

    private Long customerId;
    private String customerName;
    private String email;
    private int timeFrameMonths;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<MonthlyReward> monthlyRewards;
    private BigDecimal totalRewardPoints;
    private BigDecimal totalAmountSpent;
    private int totalTransactions;

    public RewardsResponse() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTimeFrameMonths() {
        return timeFrameMonths;
    }

    public void setTimeFrameMonths(int timeFrameMonths) {
        this.timeFrameMonths = timeFrameMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<MonthlyReward> getMonthlyRewards() {
        return monthlyRewards;
    }

    public void setMonthlyRewards(List<MonthlyReward> monthlyRewards) {
        this.monthlyRewards = monthlyRewards;
    }

    public BigDecimal getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void setTotalRewardPoints(BigDecimal totalRewardPoints) {
        this.totalRewardPoints = totalRewardPoints;
    }

    public BigDecimal getTotalAmountSpent() {
        return totalAmountSpent;
    }

    public void setTotalAmountSpent(BigDecimal totalAmountSpent) {
        this.totalAmountSpent = totalAmountSpent;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
}