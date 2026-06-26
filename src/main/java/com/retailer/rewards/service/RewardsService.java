package com.retailer.rewards.service;

import com.retailer.rewards.dto.MonthlyReward;
import com.retailer.rewards.dto.RewardsResponse;
import com.retailer.rewards.dto.TransactionDetail;
import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.exception.InvalidRequestException;
import com.retailer.rewards.model.Customer;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Core service responsible for calculating customer reward points.
 * Orchestrates transaction fetching and reward point computation.
 */
@Service
public class RewardsService {

    private static final Logger logger = LoggerFactory.getLogger(RewardsService.class);
    private static final int MAX_MONTHS = 24;
    private static final int MIN_MONTHS = 1;
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;
    private final RewardsCalculator rewardsCalculator;

    public RewardsService(CustomerRepository customerRepository,
                          TransactionService transactionService,
                          RewardsCalculator rewardsCalculator) {
        this.customerRepository = customerRepository;
        this.transactionService = transactionService;
        this.rewardsCalculator = rewardsCalculator;
    }

    /**
     * Calculates reward points for a given customer over a specified number of months.
     * * @param customerId the customer ID
     * @param months     the number of months to look back from today
     * @return the complete rewards response with monthly breakdown
     */
    public RewardsResponse calculateRewards(Long customerId, int months) {
        logger.info("Calculating rewards for customer ID: {} over {} months", customerId, months);

        validateMonths(months);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(months);

        // Fetch transactions
        List<Transaction> transactions =
                transactionService.fetchTransactions(customerId, startDate, endDate);

        // Group transactions by month
        Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getTransactionDate()),
                        TreeMap::new,
                        Collectors.toList()
                ));

        // Build monthly rewards breakdown
        List<MonthlyReward> monthlyRewards = buildMonthlyRewards(transactionsByMonth);

        // Calculate totals
        BigDecimal totalPoints = monthlyRewards.stream().map(MonthlyReward::getPointsEarned).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalSpent = monthlyRewards.stream().map(MonthlyReward::getTotalSpent).reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalTransactionCount = monthlyRewards.stream().mapToInt(MonthlyReward::getTransactionCount).sum();

        // Build response
        RewardsResponse response = new RewardsResponse();
        response.setCustomerId(customer.getId());
        response.setCustomerName(customer.getCustomerName());
        response.setEmail(customer.getEmail());
        response.setTimeFrameMonths(months);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setMonthlyRewards(monthlyRewards);
        response.setTotalRewardPoints(totalPoints);
        response.setTotalAmountSpent(totalSpent);
        response.setTotalTransactions(totalTransactionCount);

        logger.info("Rewards calculation complete for customer ID: {}. Total points: {}", customerId, totalPoints);
        return response;
    }

    /**
     * Validates the months parameter is within acceptable range.
     */
    private void validateMonths(int months) {
        if (months < MIN_MONTHS || months > MAX_MONTHS) {
            throw new InvalidRequestException(
                    "Months parameter must be between " + MIN_MONTHS + " and " + MAX_MONTHS + ". Received: " + months
            );
        }
    }

    /**
     * Builds the monthly rewards breakdown from grouped transactions.
     */
    private List<MonthlyReward> buildMonthlyRewards(Map<YearMonth, List<Transaction>> transactionsByMonth) {
        List<MonthlyReward> monthlyRewards = new ArrayList<>();

        for (Map.Entry<YearMonth, List<Transaction>> entry : transactionsByMonth.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            List<Transaction> monthTransactions = entry.getValue();

            List<TransactionDetail> transactionDetails = monthTransactions.stream()
                    .map(this::toTransactionDetail)
                    .collect(Collectors.toList());

            BigDecimal monthPoints = transactionDetails.stream().map(TransactionDetail::getPointsEarned).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal monthSpent = monthTransactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            MonthlyReward monthlyReward = new MonthlyReward(
                    yearMonth.atDay(1).format(MONTH_FORMATTER),
                    monthTransactions.size(),
                    monthSpent,
                    monthPoints,
                    transactionDetails
            );

            monthlyRewards.add(monthlyReward);
        }

        return monthlyRewards;
    }

    /**
     * Converts a Transaction entity to a TransactionDetail DTO with calculated points.
     */
    private TransactionDetail toTransactionDetail(Transaction transaction) {
        BigDecimal points = rewardsCalculator.calculatePoints(transaction.getAmount());
        return new TransactionDetail(
                transaction.getId(),
                transaction.getTransactionDate(),
                transaction.getAmount(),
                transaction.getDescription(),
                points
        );
    }
}