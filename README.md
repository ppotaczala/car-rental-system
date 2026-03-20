# Car Rental System

Simple in-memory car rental system implemented in Java.

## Design decisions
- Reservation is made per car type, not a specific vehicle.
- Fleet capacity is limited per car type.
- Availability is determined by counting overlapping reservations.

## How it works
- Each reservation blocks one car of a given type.
- For a given request, the system:
    1. Finds all existing reservations for that car type
    2. Filters overlapping reservations
    3. Compares count with available cars
- If capacity is not exceeded → reservation is accepted
- Otherwise → reservation is rejected

## Example
Inventory:
- SEDAN: 1

Scenario:
- Reservation 1 → accepted
- Reservation 2 (same time) → rejected

## Validation rules
- Car type must not be null
- Number of days must be greater than 0
- Start date must not be null

## Assumptions
- System is in-memory only
- Reservation duration is in full days
- No timezone handling

## Limitations / Future improvements
- No persistence (DB)
- No cancellation
- No concurrency handling
- No assignment of specific vehicles
- No pricing / billing

## Architecture
- CarInventory – holds available cars per type
- CarRentalService – handles reservation logic
- Reservation – represents a booking
- CarType – enum for supported vehicle types

## Running tests
mvn clean test