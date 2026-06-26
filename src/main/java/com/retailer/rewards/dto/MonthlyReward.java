package com.retailer.rewards.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing reward points earned in a specific month,
 * including the list of transactions for that month.
 */
public class MonthlyReward {

    private String month;
    private int transactionCount;
    private BigDecimal totalSpent;
    private BigDecimal pointsEarned;
    private List<TransactionDetail> transactions;

    public MonthlyReward() {
    }

    public MonthlyReward(String month, int transactionCount, BigDecimal totalSpent, BigDecimal pointsEarned, List<TransactionDetail> transactions) {
        this.month = month;
        this.transactionCount = transactionCount;
        this.totalSpent = totalSpent;
        this.pointsEarned = pointsEarned;
        this.transactions = transactions;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }

    public BigDecimal getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public List<TransactionDetail> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDetail> transactions) {
        this.transactions = transactions;
    }
}