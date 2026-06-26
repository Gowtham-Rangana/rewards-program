package com.retailer.rewards.service;

import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service handling transaction retrieval operations.
 * Demonstrates asynchronous processing simulation.
 */
@Service
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Fetches transactions for a customer within a date range.
     * Simulates potential network latency or a slow external API/DB dependency.
     */

    public List<Transaction> fetchTransactions(Long customerId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching transactions for customer ID: {} from {} to {}", customerId, startDate, endDate);

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndTransactionDateBetween(customerId, startDate, endDate);
        logger.debug("Fetched {} transactions for customer ID: {}", transactions.size(), customerId);

        return transactions;
    }
}