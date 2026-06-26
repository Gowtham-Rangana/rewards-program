# Customer Rewards Program

A RESTful Spring Boot application that calculates reward points for customers based on their purchase transactions.

## Table of Contents

- [Overview](#overview)
- [Reward Rules](#reward-rules)
- [Technical Stack](#technical-stack)
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Sample Data](#sample-data)

---

## Overview

A retailer offers a rewards program to its customers, awarding points based on each recorded purchase. This application provides a REST API to calculate and retrieve reward points earned by customers over a configurable time period, with a detailed monthly breakdown.

## Reward Rules

Points are calculated per transaction using two tiers:

| Spend Range       | Points Earned          |
|-------------------|------------------------|
| Over $100         | 2 points per dollar    |
| $50.01 - $100.00  | 1 point per dollar     |
| $0 - $50.00       | 0 points               |

*Example:* A $120.75 purchase earns:

- ($120.75 - $100) × 2 = 41.50 points (tier 2)
- ($100 - $50) × 1 = 50 points (tier 1)
- *Total: 91.50 points*

## Technical Stack

| Component  | Technology                                         |
|------------|----------------------------------------------------|
| Language   | Java 8                                             |
| Framework  | Spring Boot 2.7.18                                 |
| Database   | H2 (in-memory)                                     |
| ORM        | Spring Data JPA / Hibernate                        |
| Build Tool | Maven                                              |
| Testing    | JUnit 5, Mockito, MockMvc                          |
| Logging    | SLF4J + Logback                                    |
| Precision  | BigDecimal for all monetary and points calculation |

### Key Design Decisions

- *BigDecimal Precision*: All monetary amounts and reward points use BigDecimal to prevent floating-point truncation. A $120.75 purchase correctly earns 91.50 points, not 90.

- *Separated Calculation Logic*: RewardsCalculator is a standalone component for testability and single responsibility.

- *Dynamic Time Frame*: The API accepts a configurable months parameter (1–24) instead of hardcoding 3 months.

- *Global Exception Handling*: @RestControllerAdvice provides consistent error responses with proper HTTP status codes.

- *Simplified Calculation*: Points formula uses `2 * max(amount - 100, 0) + max(min(amount, 100) - 50, 0)` - a single expression with no branching.

## API Documentation

### Get Customer Rewards

Calculates reward points for a specific customer over a given time period.

*Endpoint:*

http
GET /api/v1/rewards/{customerId}?months={months}


*Parameters:*

| Parameter  | Type | Location | Required | Default | Description |
|------------|------|----------|----------|---------|-------------|
| customerId | Long | Path     | Yes      | -       | Unique customer identifier |
| months     | int  | Query    | No       | 3       | Number of months to look back (1–24) |

**Success Response (200 OK):**

```json
{
  "customerId": 1,
  "customerName": "Alice Johnson",
  "email": "alice.johnson@email.com",
  "timeFrameMonths": 3,
  "startDate": "2024-01-15",
  "endDate": "2024-04-15",
  "monthlyRewards": [
    {
      "month": "February 2024",
      "transactionCount": 3,
      "totalSpent": 240.50,
      "pointsEarned": 115,
      "transactions": [
        {
          "transactionId": 1,
          "date": "2024-02-05",
          "amount": 120.75,
          "description": "Electronics purchase",
          "pointsEarned": 91.50
        }
      ]
    }
  ],
  "totalRewardPoints": 366.50,
  "totalAmountSpent": 850.50,
  "totalTransactions": 9
}
```

**Error Responses:**

| Status | Condition | Example Message |
|--------|-----------|-----------------|
| 400 | Invalid months parameter | "Months parameter must be between 1 and 24" |
| 404 | Customer not found | "Customer not found with ID: 999" |
| 500 | Unexpected server error | "An unexpected error occurred. Please try again." |

## Getting Started

### Prerequisites

- Java 8 (JDK 1.8)
- Maven 3.6+

### Build & Run

# Clone the repository
git clone [https://github.com/your-username/rewards-program.git](https://github.com/your-username/rewards-program.git)
cd rewards-program

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

The application starts on http://localhost:8080.

### Test the API

# Get rewards for customer 1 (default 3 months)
curl http://localhost:8080/api/v1/rewards/1

# Get rewards for customer 2 over 6 months
curl http://localhost:8080/api/v1/rewards/2?months=6

# Test error: non-existent customer
curl http://localhost:8080/api/v1/rewards/999

### H2 Console

Access the H2 database console at: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:rewardsdb
- Username: sa
- Password: (empty)

## Running Tests

# Run all tests
mvn test

# Run with verbose output
mvn test -Dtest.verbose=true

### Test Coverage

| Test Class | Scenarios Covered |
|------------|-------------------|
| RewardsCalculatorTest | Zero, negative, under $50, exactly $50, $51, between $50-$100, exactly $100, $101, over $100, large amount, decimal amounts |
| RewardsServiceTest | Multiple transactions, no transactions, invalid customer, invalid months (min/max), single transaction, time frame validation |
| RewardsControllerTest | Valid request, default months, customer not found (404), invalid months (400), custom months parameter |
| RewardsApplicationTest | Application context loads successfully |

## Sample Data

The application loads sample data on startup with 3 customers and transactions spanning 3 months:

| Customer | Transactions | Scenarios Demonstrated |
|----------|--------------|------------------------|
| Alice Johnson | 9 | Mix of all tiers ($30-$200) |
| Bob Martinez | 6 | High-value purchases, below-threshold buys |
| Carol Williams | 7 | Boundary values ($50, $95, $175) |
