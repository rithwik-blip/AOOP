package casestudy4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object representing a hotel stay date range.
 * Invariants:
 * 1. checkIn and checkOut cannot be null.
 * 2. checkIn must be strictly before checkOut (at least 1 night).
 * 3. Consecutive stays: checkOut on day D and checkIn on day D do NOT overlap (half-open interval [checkIn, checkOut)).
 */
public final class DateRange {
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    private DateRange(LocalDate checkIn, LocalDate checkOut) {
        Objects.requireNonNull(checkIn, "Check-in date cannot be null");
        Objects.requireNonNull(checkOut, "Check-out date cannot be null");
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException(
                    "Invalid stay period: check-in (" + checkIn + ") must be strictly before check-out (" + checkOut + ")");
        }
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public static DateRange of(LocalDate checkIn, LocalDate checkOut) {
        return new DateRange(checkIn, checkOut);
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /**
     * Checks if this date range overlaps with another date range.
     * Uses standard interval overlap logic: [StartA, EndA) overlaps [StartB, EndB)
     * iff StartA < EndB AND EndA > StartB.
     * If one guest checks out on the day another guest checks in, they do NOT overlap.
     */
    public boolean overlapsWith(DateRange other) {
        Objects.requireNonNull(other, "Other date range cannot be null");
        return this.checkIn.isBefore(other.checkOut) && this.checkOut.isAfter(other.checkIn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DateRange that)) return false;
        return checkIn.equals(that.checkIn) && checkOut.equals(that.checkOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut);
    }

    @Override
    public String toString() {
        return checkIn + " to " + checkOut + " (" + getNights() + " nights)";
    }
}
