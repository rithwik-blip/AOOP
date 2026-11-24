package casestudy1;

import java.util.Objects;

/**
 * Value Object representing a percentage discount or rate.
 * Invariant: basis points must be between 0 and 10,000 (0.00% to 100.00%).
 * Uses basis points (1 basis point = 0.01%) for exact integer arithmetic without float drift.
 */
public final class Percentage {
    private final int basisPoints; // 0 to 10000

    private Percentage(int basisPoints) {
        this.basisPoints = basisPoints;
    }

    public static Percentage ofBasisPoints(int bp) {
        if (bp < 0 || bp > 10_000) {
            throw new IllegalArgumentException("Percentage basis points must be between 0 and 10000, got: " + bp);
        }
        return new Percentage(bp);
    }

    public static Percentage ofPercent(int percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100, got: " + percent);
        }
        return new Percentage(percent * 100);
    }

    public int getBasisPoints() {
        return basisPoints;
    }

    public double asPercent() {
        return basisPoints / 100.0;
    }

    /**
     * Applies this percentage discount to Money and returns the discount amount.
     */
    public Money applyTo(Money money) {
        Objects.requireNonNull(money, "Money cannot be null");
        // basis points: 10000 = 100.00%
        // Rounding: HALF_EVEN (banker's rounding)
        long minor = money.getMinorAmount();
        long product = Math.multiplyExact(minor, basisPoints);
        long discountMinor = Math.round((double) product / 10_000.0);
        return Money.ofMinor(discountMinor, money.getCurrency());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Percentage that)) return false;
        return basisPoints == that.basisPoints;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(basisPoints);
    }

    @Override
    public String toString() {
        return (basisPoints / 100.0) + "%";
    }
}
