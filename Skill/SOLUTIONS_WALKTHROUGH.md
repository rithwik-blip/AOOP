# ADVANCED OBJECT-ORIENTED PROGRAMMING (AOOP)
## Domain Modeling – Self-Enforcing Objects, Entities vs Values, and Making Illegal States Unrepresentable

**Directory Location:** `C:\Users\Rithu\OneDrive\5 sem\AOOP`

This directory contains production-quality Java implementations and thorough domain analysis addressing all assigned case studies:
- **Case Study 1:** Money and Pricing in an E-Commerce Catalog
- **Case Study 3:** Bank Account and Money Transfers
- **Case Study 4:** Hotel Reservations and Date Ranges
- **Case Study 5:** User Accounts, Email Verification and Contact Details

---

## 1. Directory Structure

```
C:\Users\Rithu\OneDrive\5 sem\AOOP\
│
├── casestudy1\
│   ├── Currency.java                 (Enumeration of currencies with decimal precision/minor units)
│   ├── Quantity.java                 (Value Object: positive whole number >= 1)
│   ├── Percentage.java               (Value Object: discount rate 0-100% in basis points)
│   ├── Money.java                    (Value Object: non-negative minor units integer, safe arithmetic)
│   ├── Product.java                  (Entity: SKU-identified item with effective price calculation)
│   ├── Cart.java                     (Aggregate Root: maintains currency consistency and line totals)
│   └── CaseStudy1Demo.java           (Interactive execution driver verifying invariants)
│
├── casestudy3\
│   ├── AccountNumber.java            (Value Object: validated bank account number)
│   ├── Currency.java                 (Enumeration of ISO currencies)
│   ├── PositiveMoney.java            (Value Object: strictly positive financial transaction amount > 0)
│   ├── OverdraftLimit.java           (Value Object: non-negative overdraft limit)
│   ├── AccountStatus.java            (Enum: ACTIVE, FROZEN, CLOSED)
│   ├── EntryType.java                (Enum: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT)
│   ├── LedgerEntry.java              (Value Object: immutable audit record of balance mutation)
│   ├── BankAccount.java              (Aggregate Root: self-enforcing state machine & balance invariants)
│   ├── TransferStatus.java           (Enum: INITIATED, COMPLETED, FAILED)
│   ├── FundTransfer.java             (Entity: lifecycle & audit of inter-account transfers)
│   ├── TransferService.java          (Domain Service: 2-phase transfer with deadlock-safe locking & rollback)
│   └── CaseStudy3Demo.java           (Execution driver demonstrating all business invariants)
│
├── casestudy4\
│   ├── DateRange.java                (Value Object: half-open stay period [checkIn, checkOut), overlap logic)
│   ├── RoomId.java                   (Value Object: unique room identifier)
│   ├── ReservationId.java            (Value Object: unique reservation identifier)
│   ├── CancellationPolicy.java       (Value Object: notice days, late fee percentage, refund rules)
│   ├── PaymentGuarantee.java         (Value Object: pre-authorized payment reference required for confirmation)
│   ├── Guest.java                    (Entity: hotel customer details)
│   ├── ReservationStatus.java        (Enum: REQUESTED, CONFIRMED, CHECKED_IN, COMPLETED, CANCELLED)
│   ├── Reservation.java              (Entity: self-enforcing state machine preventing illegal transitions)
│   ├── Room.java                     (Aggregate Root: room inventory & overlap prevention boundary)
│   └── CaseStudy4Demo.java           (Execution driver demonstrating double-booking & cancellation enforcement)
│
├── casestudy5\
│   ├── UserId.java                   (Value Object: unique user identifier)
│   ├── EmailAddress.java             (Value Object: RFC-compliant, lower-case normalized email)
│   ├── PhoneNumber.java              (Value Object: E.164 international standard normalized phone)
│   ├── PostalAddress.java            (Value Object: complete, non-blank street/city/code/country)
│   ├── VerificationToken.java        (Value Object: cryptographically secure token with expiry check)
│   ├── EmailVerificationState.java   (Sealed algebraic hierarchy: Unverified, PendingVerification, Verified)
│   ├── AccountStatus.java            (Enum: ACTIVE, SUSPENDED, DELETED)
│   ├── UserAccount.java              (Aggregate Root: strongly typed self-enforcing user entity)
│   ├── UserRegistrationService.java  (Domain Service: cross-aggregate email uniqueness & casing enforcement)
│   └── CaseStudy5Demo.java           (Execution driver demonstrating token expiry, casing normalization, suspension)
│
└── SOLUTIONS_WALKTHROUGH.md          (This detailed document)
```

---

## 2. Compilation and Execution

All source code compiles cleanly using Java 17+ / Java 21:
```powershell
# Navigate to the folder
cd "C:\Users\Rithu\OneDrive\5 sem\AOOP"

# Compile all classes
javac (Get-ChildItem -Path . -Recurse -Filter "*.java").FullName

# Run individual demonstrations
java casestudy1.CaseStudy1Demo
java casestudy3.CaseStudy3Demo
java casestudy4.CaseStudy4Demo
java casestudy5.CaseStudy5Demo
```

---

## 3. Case Study 3: Bank Account and Money Transfers (Detailed Solutions)

### Q1. Identification of Domain Invariants
1. **Invariant 1: Strictly Positive Transaction Amounts**
   - *Rule:* Any transaction (deposit, withdrawal, transfer) must specify an amount strictly greater than zero ($> 0$).
   - *Importance:* Financial systems cannot process zero or negative delta values without corrupting audit trails.
   - *Consequences of Violation:* Passing a negative withdrawal amount would act as an unintended deposit, allowing malicious balance inflation.
2. **Invariant 2: Overdraft Limit Ceiling**
   - *Rule:* An account balance can never drop below negative overdraft limit ($\text{balance} \ge -\text{overdraftLimit}$).
   - *Importance:* Limits the credit exposure and financial risk of the bank.
   - *Consequences of Violation:* Uncontrolled credit granting, unrecoverable bad loans, and systemic liquidity losses.
3. **Invariant 3: Account Lifecycle Restriction on Debits**
   - *Rule:* Withdrawals and outbound transfers are prohibited if the account status is `FROZEN` or `CLOSED`.
   - *Importance:* Freezing is mandated by compliance, fraud detection, and legal orders.
   - *Consequences of Violation:* Defeating regulatory/court orders, continuing fraudulent withdrawals on compromised accounts.
4. **Invariant 4: Balance = Sum of Immutable Ledger Entries**
   - *Rule:* An account's current balance must equal the exact summation of all historically recorded ledger entries.
   - *Importance:* Ensures traceability, regulatory audit compliance, and repudiation defense.
   - *Consequences of Violation:* "Phantom money" bugs where the account display shows funds not backed by any transaction history.
5. **Invariant 5: Currency Homogeneity**
   - *Rule:* Accounts, ledger entries, and transaction amounts must share the same ISO currency.
   - *Importance:* A unit of USD cannot be directly subtracted from or added to INR.
   - *Consequences of Violation:* Catastrophic conversion loss (e.g. subtracting 50 USD directly from 50 INR leaving 0 balance).
6. **Invariant 6: Conservation of Money in Fund Transfers (Atomicity)**
   - *Rule:* In an internal transfer between Account A and Account B, the total money in the system must be conserved ($\Delta \text{Balance}_A + \Delta \text{Balance}_B = 0$).
   - *Importance:* Money cannot be destroyed or created out of thin air during system failures.
   - *Consequences of Violation:* Customer loses ₹5,000 when source is debited but destination is never credited.

### Q2. Entities vs Value Objects Classification
| Concept | Kind | Justification based on Identity, Equality, Lifecycle & Mutability |
| :--- | :--- | :--- |
| **Bank Account** | **Entity** (Aggregate Root) | Has a persistent identity (`AccountNumber`). Its balance and status evolve over time, but it remains the same account. Equality is based strictly on `AccountNumber`. |
| **Account Number** | **Value Object** | Immutable string adhering to banking format (8-16 chars). Has no separate lifecycle or mutable state. Two account numbers with identical string values are interchangeable. |
| **PositiveMoney** | **Value Object** | Completely immutable. Defined solely by currency and minor amount. ₹500 is equal to any other ₹500. No lifecycle. |
| **Ledger Entry** | **Value Object** | Represents a factual, historical event recorded at a point in time. Completely immutable; never updated or deleted. |
| **Overdraft Limit** | **Value Object** | Represents a non-negative limit value. Completely immutable. Can be replaced as a whole on the account. |
| **Fund Transfer** | **Entity** | Has unique tracking identity (`transferId`), a lifecycle (`INITIATED`, `COMPLETED`, `FAILED`), and audit timestamps. |

### Q3. Self-Enforcing Objects and Java Design
- **Rejection of Invalid Amounts:** Made illegal states unrepresentable by introducing `PositiveMoney`. The constructor throws `IllegalArgumentException` if `minorAmount <= 0`.
- **Status Checks:** `BankAccount.withdraw()` inspects `status`:
  ```java
  if (status == AccountStatus.FROZEN || status == AccountStatus.CLOSED) {
      throw new IllegalStateException("Withdrawal blocked: Account is " + status);
  }
  ```
- **Overdraft Check:**
  ```java
  if (this.balanceMinor - amount.getMinorAmount() < -overdraftLimit.getMinorLimit()) {
      throw new IllegalStateException("Withdrawal rejected! Exceeds overdraft limit.");
  }
  ```
- **Encapsulation:** All internal fields (`balanceMinor`, `status`, `ledger`) are `private`. Balance is modified only through `deposit()` and `withdraw()`. The ledger returns an unmodifiable list copy (`Collections.unmodifiableList`).

### Q4. Money Transfer and Aggregate Consistency
- **Problems:** Asymmetry / money loss. Account A is debited ₹5,000, but system crash leaves Account B uncredited. ₹5,000 disappeared from the bank's circulating balance.
- **Invariants Violated:** Financial conservation invariant and ledger consistency.
- **Preventing Money Loss:**
  1. *Same Banking System:* `TransferService` executes a 2-phase synchronized transaction. If crediting Account B fails, an automatic **compensating transaction** (reversal deposit) is executed on Account A.
  2. *Deadlock Prevention:* Locks on accounts are ordered deterministically by comparing `AccountNumber` values.
  3. *Different Banking Systems (Distributed Architecture):* Two separate databases cannot share synchronized memory locks. They require the **Saga Pattern** or an **Outbox Pattern** with a two-phase commit / idempotency keys. Account A enters `PENDING_TRANSFER` (amount reserved), message is sent over inter-bank network (NEFT/RTGS/SWIFT). Upon ACK from destination bank, Account A ledger finalizes; on failure or timeout, reservation is released.

### Q5. Ledger Management
- **Information Contained:** `entryId` (UUID), `timestamp` (Instant), `type` (DEPOSIT, WITHDRAWAL, etc.), `amount` (PositiveMoney), `balanceAfterMinor` (long), `description`.
- **Immutability:** Existing ledger entries must **never** be edited or deleted. An accounting ledger is append-only. Corrections must be handled by appending a new *compensating entry* (reversal).
- **Consistency:** `balanceMinor` is updated within the same synchronized method that appends to `ledger`.
- **Large Ledger Challenges:** Loading thousands of entries slows performance. Solution: Snapshotting (periodically archive balance snapshots, e.g., monthly closing balance, and query delta transactions from the latest snapshot).

---

## 4. Case Study 4: Hotel Reservations and Date Ranges (Detailed Solutions)

### Q1. Domain Invariants and Value Objects
1. **Invariants:**
   - *Minimum Stay:* `checkIn` must be strictly before `checkOut` (minimum 1 night).
   - *No Overlap:* A room cannot have two active reservations whose stay intervals overlap.
   - *Consecutive Stays:* Check-out on date $D$ by Guest 1 and check-in on date $D$ by Guest 2 must be valid and non-overlapping ($[D_1, D_2) \cap [D_2, D_3) = \emptyset$).
   - *Payment Guarantee:* Confirmation requires an authorized `PaymentGuarantee`.
   - *Check-in Precondition:* A guest can only check in if the reservation is `CONFIRMED`.
   - *No Post-Check-in Cancellation:* Once status is `CHECKED_IN` or `COMPLETED`, cancellation and automated refunds are prohibited.
2. **Value Objects:**
   - `DateRange`: Encapsulates start date, end date, and overlap calculation.
   - `RoomId`: Identifies physical room number.
   - `ReservationId`: Identifies the reservation.
   - `PaymentGuarantee`: Immutable payment authorization token.
   - `CancellationPolicy`: Immutable rules governing notice days and penalty percentages.

### Q2. Designing DateRange and Interval Logic
The `DateRange` class models stay duration as a half-open interval $[checkIn, checkOut)$:
```java
public boolean overlapsWith(DateRange other) {
    return this.checkIn.isBefore(other.checkOut) && this.checkOut.isAfter(other.checkIn);
}
```
If Guest 1 stays Dec 10 to Dec 15, and Guest 2 stays Dec 15 to Dec 20:
- Guest 1: $[10, 15)$
- Guest 2: $[15, 20)$
- Overlap check: `10 < 20` (true) AND `15 > 15` (false) $\rightarrow$ Returns `false`! Concurrency and consecutive bookings work accurately without special-case branching.

### Q3. Entity Classification and Aggregate Boundary
- **Entities:** `Guest`, `Reservation`, `Room` (Aggregate Root).
- **Value Objects:** `RoomId`, `ReservationId`, `DateRange`, `CancellationPolicy`, `PaymentGuarantee`.
- **Consistency Boundary:** `Room` is the **Aggregate Root**. Room availability cannot be enforced by `Reservation` alone because checking overlap requires visibility across *all* active reservations for that physical room. `Room.book()` locks the room and inspects all active reservations before appending a new one.

### Q4. Reservation Lifecycle & State Machine
- **Permitted Transitions:**
  - `REQUESTED` $\rightarrow$ `CONFIRMED` (Mandatory `PaymentGuarantee`)
  - `CONFIRMED` $\rightarrow$ `CHECKED_IN`
  - `CHECKED_IN` $\rightarrow$ `COMPLETED`
  - `REQUESTED` $\rightarrow$ `CANCELLED`
  - `CONFIRMED` $\rightarrow$ `CANCELLED` (Refund percentage calculated via `CancellationPolicy`)
- **Prohibited Transitions:**
  - `CHECKED_IN` $\rightarrow$ `CANCELLED` (Throws `IllegalStateException`)
  - `COMPLETED` $\rightarrow$ `CANCELLED` (Throws `IllegalStateException`)
  - `REQUESTED` $\rightarrow$ `CHECKED_IN` (Bypassing payment confirmation blocked)

### Q5. Concurrent Reservations & Persistence Protection
- **Race Condition:** Two receptionists submit overlapping bookings at the same instant. Both read the room as "available" before either saves.
- **Application Level:** Synchronized methods on `Room` aggregate root.
- **Persistence Level:**
  1. *Optimistic Locking:* Add a `@Version int version` column to `Room`. If two threads update concurrently, the second commit fails with an `OptimisticLockException`.
  2. *Database Exclusion Constraints (PostgreSQL):*
     ```sql
     ALTER TABLE reservations ADD CONSTRAINT no_overlapping_stays
     EXCLUDE USING gist (room_id WITH =, stay_interval WITH &&);
     ```

---

## 5. Case Study 5: User Accounts, Email Verification and Contact Details (Detailed Solutions)

### Q1. Identification of Invariants
1. **Email RFC Format:** Must match standardized regex (`local@domain.tld`). Prevents garbage inputs.
2. **Email Normalization:** Must be stored in lowercase (`user@example.com` == `User@EXAMPLE.com`). Prevents duplicate spoofed accounts.
3. **Algebraic Verification Consistency:** Verified status *cannot* exist without a valid, non-null `Instant verifiedAt` timestamp.
4. **Token Expiry:** Verification token must have an `expiresAt` instant. An expired token cannot verify an account.
5. **E.164 International Phone Format:** Normalized to strip hyphens/spaces and ensure country code format (`+919876543210`).
6. **Postal Address Atomicity:** Street, city, postal code, and country are mutually mandatory. Incomplete postal addresses are rejected.
7. **Account Operational Gate:** `SUSPENDED` and `DELETED` accounts cannot log in or initiate state changes.

### Q2. Entities vs Value Objects Classification
- **User Account:** **Entity** (Aggregate Root). Has long-lived `UserId`. Profile attributes mutate over time.
- **UserId:** **Value Object**. Immutable string/UUID.
- **EmailAddress:** **Value Object**. Immutable, normalized lower-case string.
- **PhoneNumber:** **Value Object**. Immutable, normalized E.164 string.
- **PostalAddress:** **Value Object**. Immutable composite of street, city, postal code, country.
- **VerificationToken:** **Value Object**. Immutable token hash and expiry instant.

### Q3. Making Illegal States Unrepresentable
**Flaws in Naive Model (primitives & nulls):**
- In naive code (`boolean emailVerified, Instant verifiedAt, String token`), 8 combinations exist, 6 of which are contradictory (e.g. `emailVerified = true`, `verifiedAt = null`).
- Primitive strings allow malformed emails, unformatted phone numbers, and partial addresses.

**Improved Object-Oriented Design:**
Using Java sealed interfaces (`EmailVerificationState`):
```java
public sealed interface EmailVerificationState permits
        EmailVerificationState.Unverified,
        EmailVerificationState.PendingVerification,
        EmailVerificationState.Verified {

    record Unverified() implements EmailVerificationState {}
    record PendingVerification(VerificationToken token) implements EmailVerificationState {}
    record Verified(Instant verifiedAt) implements EmailVerificationState {}
}
```
*Result:* It is compiler-enforced and mathematically impossible to be in `Verified` state without providing an `Instant`.

### Q4. Email Verification & Account Lifecycle
- **Registration:** Initializes state to `Unverified`.
- **Token Generation:** `initiateEmailVerification()` creates a 32-byte secure token with configurable TTL (e.g., 15 minutes).
- **Token Validation:** `confirmEmailVerification(candidateToken, now)` checks `isExpired(now)`. If expired, transitions state to require a fresh token.
- **Email Modification:** If a verified user changes email, invariant resets verification state to `PendingVerification` for the new email.
- **Suspension:** `canLogin()` returns `false` if `status == SUSPENDED || status == DELETED`.

### Q5. Cross-Aggregate Rules & Design Evaluation
- **Is Email Uniqueness an Invariant of `UserAccount`?**
  No! An individual `UserAccount` only knows its own state. It cannot know what other users exist without querying a repository. Enforcing email uniqueness is a **Domain Service** / **Application Service** concern (`UserRegistrationService`).
- **Concurrent Registration Protection:** A unique database index on `LOWER(email)`:
  ```sql
  CREATE UNIQUE INDEX idx_users_email_lower ON user_accounts (LOWER(email));
  ```
- **Trade-off Evaluation:**
  - *Advantage of Strongly Typed Model:* Total elimination of illegal/impossible states at compile time; zero defensive null-checks; self-documenting code.
  - *Disadvantage:* Slightly more boilerplate classes (e.g., records and sealed types) and requires custom ORM/Jackson converters when mapping to flat relational tables or JSON payloads.

---

## 6. Summary of Key Principles Demonstrated

1. **Self-Enforcing Invariants:** Validation is enforced in private constructors and factory methods. An object cannot exist in memory in an invalid state.
2. **Entities vs Value Objects:** Distinguish concepts with continuous lifecycle and identity (`BankAccount`, `Room`, `UserAccount`) from immutable property descriptors (`Money`, `DateRange`, `EmailAddress`).
3. **Aggregate Roots & Consistency Boundaries:** Aggregates (`Cart`, `BankAccount`, `Room`, `UserAccount`) encapsulate internal collections and protect cross-entity business rules.
4. **Making Illegal States Unrepresentable:** Using algebraic types, sealed hierarchies, and domain types instead of primitive obsession, boolean flags, and nullable fields.
