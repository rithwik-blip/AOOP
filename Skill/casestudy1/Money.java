package casestudy1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing an exact non-negative monetary amount.
 * Invariants:
 * - Amount cannot be negative.
 * - Currency is mandatory and non-null.
 * - Arithmetic between different currencies is strictly rejected.
 * - Stored in minor units (cents, paise) as long integer to prevent binary floating point drift.
 */
public final class Money implements Comparable<Money> {
    private final long minorAmount;
    private final Currency currency;

    private Money(long minorAmount, Currency currency) {
        if (minorAmount < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative: " + minorAmount);
        }
        this.minorAmount = minorAmount;
        this.currency = Objects.requireNonNull(currency, "Currency cannot be null");
    }

    public static Money ofMinor(long minorAmount, Currency currency) {
        return new Money(minorAmount, currency);
    }

    public static Money of(String decimalStr, Currency currency) {
        Objects.requireNonNull(decimalStr, "Amount string cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        BigDecimal bd = new BigDecimal(decimalStr);
        if (bd.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money cannot be negative: " + decimalStr);
        }
        BigDecimal scaled = bd.setScale(currency.getMinorUnits(), RoundingMode.UNNECESSARY);
        long minor = scaled.movePointRight(currency.getMinorUnits()).longValueExact();
        return new Money(minor, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(0, currency);
    }

    public long getMinorAmount() {
        return minorAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money plus(Money other) {
        requireSameCurrency(other);
        return new Money(Math.addExact(this.minorAmount, other.minorAmount), currency);
    }

    public Money minus(Money other) {
        requireSameCurrency(other);
        if (this.minorAmount < other.minorAmount) {
            throw new IllegalArgumentException("Cannot subtract " + other + " from " + this + " (result would be negative)");
        }
        return new Money(this.minorAmount - other.minorAmount, currency);
    }

    public Money minusPercentage(Percentage percentage) {
        Money discount = percentage.applyTo(this);
        return this.minus(discount);
    }

    public Money times(Quantity quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null");
        return new Money(Math.multiplyExact(this.minorAmount, quantity.value()), currency);
    }

    private void requireSameCurrency(Money other) {
        Objects.requireNonNull(other, "Operand cannot be null");
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currency mismatch: cannot operate on " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public int compareTo(Money o) {
        requireSameCurrency(o);
        return Long.compare(this.minorAmount, o.minorAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return minorAmount == money.minorAmount && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minorAmount, currency);
    }

    @Override
    public String toString() {
        BigDecimal bd = BigDecimal.valueOf(minorAmount).movePointLeft(currency.getMinorUnits());
        return bd.toPlainString() + " " + currency.name();
    }
}
