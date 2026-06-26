package com.retailer.rewards.service;

import com.retailer.rewards.dto.MonthlyReward;
import com.retailer.rewards.dto.RewardsResponse;
import com.retailer.rewards.dto.TransactionDetail;
import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.exception.InvalidRequestException;
import com.retailer.rewards.model.Customer;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the RewardsService.
 * Tests business logic for reward calculation including edge cases and error scenarios.
 */
@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    private static final DateTimeFormatter MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private RewardsCalculator rewardsCalculator;

    @InjectMocks
    private RewardsService rewardsService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("Alice Johnson", "alice@email.com");
        testCustomer.setId(1L);
    }

    @Test
    @DisplayName("Should calculate rewards correctly with monthly breakdown and totals")
    void calculateRewards_multipleTransactions_returnsCorrectRewards() {

        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusDays(20);
        LocalDate twoMonthsAgo = today.minusDays(50);

        List<Transaction> transactions = Arrays.asList(
                createTransaction(1L, 1L, new BigDecimal("120.00"), lastMonth, "Electronics"),
                createTransaction(2L, 1L, new BigDecimal("75.00"), lastMonth.plusDays(5), "Clothing"),
                createTransaction(3L, 1L, new BigDecimal("200.00"), twoMonthsAgo, "Furniture")
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionService.fetchTransactions(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(transactions);

        when(rewardsCalculator.calculatePoints(new BigDecimal("120.00")))
                .thenReturn(new BigDecimal("90.00"));
        when(rewardsCalculator.calculatePoints(new BigDecimal("75.00")))
                .thenReturn(new BigDecimal("25.00"));
        when(rewardsCalculator.calculatePoints(new BigDecimal("200.00")))
                .thenReturn(new BigDecimal("250.00"));

        RewardsResponse response = rewardsService.calculateRewards(1L, 3);

        // Basic assertions
        assertNotNull(response);
        assertEquals(1L, response.getCustomerId());
        assertEquals("Alice Johnson", response.getCustomerName());
        assertEquals("alice@email.com", response.getEmail());
        assertEquals(3, response.getTimeFrameMonths());

        // Verify date range
        assertEquals(LocalDate.now().minusMonths(3), response.getStartDate());
        assertEquals(LocalDate.now(), response.getEndDate());

        // Verify totals
        assertEquals(new BigDecimal("365.00").stripTrailingZeros(),
                response.getTotalRewardPoints().stripTrailingZeros());

        assertEquals(new BigDecimal("395.00").stripTrailingZeros(),
                response.getTotalAmountSpent().stripTrailingZeros());

        assertEquals(3, response.getTotalTransactions());

        // Verify monthly rewards are ordered chronologically
        List<MonthlyReward> monthlyRewards = response.getMonthlyRewards();
        assertEquals(2, monthlyRewards.size());
        // First month (older) should be the one with the $200 transaction
        YearMonth olderMonth = YearMonth.from(twoMonthsAgo);
        YearMonth newerMonth = YearMonth.from(lastMonth);

        assertEquals(olderMonth.atDay(1).format(MONTH_FORMATTER),
                monthlyRewards.get(0).getMonth());
        assertEquals(newerMonth.atDay(1).format(MONTH_FORMATTER),
                monthlyRewards.get(1).getMonth());

// Verify older month details
        MonthlyReward olderMonthReward = monthlyRewards.get(0);
        assertEquals(1, olderMonthReward.getTransactionCount());
        assertEquals(new BigDecimal("200.00").stripTrailingZeros(),
                olderMonthReward.getTotalSpent().stripTrailingZeros());
        assertEquals(new BigDecimal("250.00").stripTrailingZeros(),
                olderMonthReward.getPointsEarned().stripTrailingZeros());

// Verify older month transaction detail
        TransactionDetail olderTxn = olderMonthReward.getTransactions().get(0);
        assertEquals(3L, olderTxn.getTransactionId());
        assertEquals(new BigDecimal("200.00").stripTrailingZeros(),
                olderTxn.getAmount().stripTrailingZeros());
        assertEquals("Furniture", olderTxn.getDescription());
        assertEquals(new BigDecimal("250.00").stripTrailingZeros(),
                olderTxn.getPointsEarned().stripTrailingZeros());

// Verify newer month details
        MonthlyReward newerMonthReward = monthlyRewards.get(1);
        assertEquals(2, newerMonthReward.getTransactionCount());
        assertEquals(new BigDecimal("195.00").stripTrailingZeros(),
                newerMonthReward.getTotalSpent().stripTrailingZeros());
        assertEquals(new BigDecimal("115.00").stripTrailingZeros(),
                newerMonthReward.getPointsEarned().stripTrailingZeros());
    }

    @Test
    @DisplayName("Should return zero points when customer has no transactions")
    void calculateRewards_noTransactions_returnsZeroPoints() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));
        when(transactionService.fetchTransactions(
                eq(1L),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        RewardsResponse response = rewardsService.calculateRewards(1L, 3);

        assertNotNull(response);
        assertEquals(BigDecimal.ZERO, response.getTotalRewardPoints());
        assertEquals(0, response.getTotalTransactions());
        assertEquals(BigDecimal.ZERO, response.getTotalAmountSpent());
        assertTrue(response.getMonthlyRewards().isEmpty());
    }
    @Test
    @DisplayName("Should throw CustomerNotFoundException for non-existent customer")
    void calculateRewards_invalidCustomer_throwsException() {
        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> rewardsService.calculateRewards(999L, 3));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException for months below minimum")
    void calculateRewards_monthsBelowMinimum_throwsException() {
        assertThrows(InvalidRequestException.class,
                () -> rewardsService.calculateRewards(1L, 0));
    }

    @Test
    @DisplayName("Should throw InvalidRequestException for months above maximum")
    void calculateRewards_monthsAboveMaximum_throwsException() {
        assertThrows(InvalidRequestException.class,
                () -> rewardsService.calculateRewards(1L, 25));
    }

    @Test
    @DisplayName("Should correctly set the time frame in the response")
    void calculateRewards_validRequest_setsCorrectTimeFrame() {
        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));
        when(transactionService.fetchTransactions(
                eq(1L),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        RewardsResponse response = rewardsService.calculateRewards(1L, 6);

        assertEquals(6, response.getTimeFrameMonths());
        assertEquals(LocalDate.now().minusMonths(6), response.getStartDate());
        assertEquals(LocalDate.now(), response.getEndDate());
    }

    @Test
    @DisplayName("Should group multiple transactions in the same month correctly")
    void calculateRewards_multipleTransactionsSameMonth_groupsCorrectly() {
        LocalDate today = LocalDate.now();

        List<Transaction> transactions = Arrays.asList(
                createTransaction(1L, 1L, new BigDecimal("120.00"),
                        today.minusDays(5), "Purchase A"),
                createTransaction(2L, 1L, new BigDecimal("75.00"),
                        today.minusDays(10), "Purchase B"),
                createTransaction(3L, 1L, new BigDecimal("200.00"),
                        today.minusDays(12), "Purchase C"),
                createTransaction(4L, 1L, new BigDecimal("50.00"),
                        today.minusDays(45), "Purchase D")
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));
        when(transactionService.fetchTransactions(
                eq(1L),
                any(LocalDate.class),
                any(LocalDate.class)))
                .thenReturn(transactions);

        when(rewardsCalculator.calculatePoints(new BigDecimal("120.00")))
                .thenReturn(new BigDecimal("90.00"));
        when(rewardsCalculator.calculatePoints(new BigDecimal("75.00")))
                .thenReturn(new BigDecimal("25.00"));
        when(rewardsCalculator.calculatePoints(new BigDecimal("200.00")))
                .thenReturn(new BigDecimal("250.00"));
        when(rewardsCalculator.calculatePoints(new BigDecimal("50.00")))
                .thenReturn(BigDecimal.ZERO);

        RewardsResponse response = rewardsService.calculateRewards(1L, 3);

        assertNotNull(response);
        assertEquals(2, response.getMonthlyRewards().size());

// Find the month bucket with 3 transactions (current month)
        MonthlyReward currentMonthReward = response.getMonthlyRewards().stream()
                .filter(m -> m.getTransactionCount() == 3)
                .findFirst()
                .orElse(null);

        assertNotNull(currentMonthReward);
        assertEquals(3, currentMonthReward.getTransactionCount());
        assertEquals(new BigDecimal("395.00").stripTrailingZeros(),
                currentMonthReward.getTotalSpent().stripTrailingZeros());
        assertEquals(new BigDecimal("365.00").stripTrailingZeros(),
                currentMonthReward.getPointsEarned().stripTrailingZeros());
        assertEquals(3, currentMonthReward.getTransactions().size());

// Verify each transaction detail is present
        List<TransactionDetail> details = currentMonthReward.getTransactions();
        assertTrue(details.stream().anyMatch(d -> d.getTransactionId().equals(1L)));
        assertTrue(details.stream().anyMatch(d -> d.getTransactionId().equals(2L)));
        assertTrue(details.stream().anyMatch(d -> d.getTransactionId().equals(3L)));

// Find the other month bucket with 1 transaction
        MonthlyReward otherMonthReward = response.getMonthlyRewards().stream()
                .filter(m -> m.getTransactionCount() == 1)
                .findFirst()
                .orElse(null);

        assertNotNull(otherMonthReward);
        assertEquals(1, otherMonthReward.getTransactionCount());
        assertEquals(new BigDecimal("50.00").stripTrailingZeros(),
                otherMonthReward.getTotalSpent().stripTrailingZeros());
        assertEquals(BigDecimal.ZERO.stripTrailingZeros(),
                otherMonthReward.getPointsEarned().stripTrailingZeros());
    }

    @Test
    @DisplayName("Should handle single transaction correctly")
    void calculateRewards_singleTransaction_returnsCorrectRewards() {
        LocalDate today = LocalDate.now();
        List<Transaction> transactions = Collections.singletonList(
                createTransaction(1L, 1L, new BigDecimal("150.00"), today.minusDays(5), "Single purchase")
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionService.fetchTransactions(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(transactions);

        when(rewardsCalculator.calculatePoints(new BigDecimal("150.00")))
                .thenReturn(new BigDecimal("150.00"));

        RewardsResponse response = rewardsService.calculateRewards(1L, 1);

        assertEquals(new BigDecimal("150.00").stripTrailingZeros(),
                response.getTotalRewardPoints().stripTrailingZeros());
        assertEquals(1, response.getTotalTransactions());
    }

    /**
     * Helper method to create a Transaction with specified values.
     */
    private Transaction createTransaction(Long id, Long customerId, BigDecimal amount,
                                          LocalDate date, String description) {

        Transaction transaction = new Transaction(customerId, amount, date, description);
        transaction.setId(id);
        return transaction;
    }
}