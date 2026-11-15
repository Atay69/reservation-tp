# Reservation TP

A Java project that models student reservations for products. It implements the
reservation domain rules in an in-memory service and tests the behavior with
JUnit 5 and Mockito.

## Features

- Student, product, and reservation-order domain models.
- Reservation validation for required fields, dates, registered students and
  products, duration, student quota, and product availability.
- Automatic order references (`CMD-001`, `CMD-002`, ...).
- Rental-price, deposit, and total calculations using `BigDecimal`.
- Unit-test and code-coverage reports generated with Maven and JaCoCo.

## Business rules

- Student and product identifiers and reservation dates are required.
- A reservation cannot start in the past, and its end date cannot be before its
  start date.
- The student and product must already be registered with the reservation
  service.
- The reservation period is inclusive and cannot exceed seven days.
- A student can have at most two active reservations.
- The same product cannot be reserved for overlapping dates.
- The rental amount is the daily rate multiplied by the number of reserved
  days; the deposit is added to calculate the total.

## Technology

- Java 25
- Apache Maven
- JUnit Jupiter 5
- Mockito 5
- JaCoCo 0.8.16

## Requirements

- JDK 25
- Maven

## Run the tests

From the project root, run:

```bash
mvn clean test
```

This runs the test suite and generates the JaCoCo HTML coverage report at:

```text
target/site/jacoco/index.html
```

The current test suite contains 32 tests. The latest generated JaCoCo report
shows 100% line, instruction, and branch coverage.

## Project structure

```text
src/
├── main/java/com/tp/reservation/
│   ├── exception/   # Reservation-specific exception
│   ├── model/       # Commande, Etudiant, and Produit
│   └── service/     # Reservation rules and in-memory operations
└── test/java/com/tp/reservation/
    ├── model/       # Model unit tests
    └── service/     # Reservation service tests
```

## Scope

This project contains the reservation domain and service logic. Reservations,
students, and products are stored in memory; no database, web API, or user
interface is configured.
