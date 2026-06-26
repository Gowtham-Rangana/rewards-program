package com.retailer.rewards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing a single transaction with its earned reward points.
 */
public class TransactionDetail {

    private Long transactionId;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private BigDecimal pointsEarned;

    public TransactionDetail() {
    }

    public TransactionDetail(Long transactionId, LocalDate date, BigDecimal amount, String description, BigDecimal pointsEarned) {
        this.transactionId = transactionId;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.pointsEarned = pointsEarned;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(BigDecimal pointsEarned) {
        this.pointsEarned = pointsEarned;
    }
}