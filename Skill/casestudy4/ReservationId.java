package casestudy4;

import java.util.Objects;

/**
 * Value Object representing a Reservation Identifier.
 */
public final class ReservationId {
    private final String value;

    public ReservationId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Reservation ID cannot be blank");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservationId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
