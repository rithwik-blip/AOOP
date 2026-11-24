package casestudy1;

import java.util.Objects;

/**
 * Value Object representing a whole-number positive quantity.
 * Invariant: value >= 1. Zero or negative quantities cannot be constructed.
 */
public final class Quantity {
    private final int value;

    private Quantity(int value) {
        this.value = value;
    }

    public static Quantity of(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1, received: " + value);
        }
        return new Quantity(value);
    }

    public int value() {
        return value;
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other, "other quantity cannot be null");
        return new Quantity(Math.addExact(this.value, other.value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
