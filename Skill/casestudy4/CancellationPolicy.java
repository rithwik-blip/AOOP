package casestudy4;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object representing Hotel Cancellation Policy.
 * Invariant: Defines free cancellation window and refund percentage.
 */
public final class CancellationPolicy {
    private final int freeCancellationDaysNotice;
    private final int lateCancellationPenaltyPercent; // e.g. 50% penalty if cancelled within notice window

    public CancellationPolicy(int freeCancellationDaysNotice, int lateCancellationPenaltyPercent) {
        if (freeCancellationDaysNotice < 0) {
            throw new IllegalArgumentException("Notice days cannot be negative");
        }
        if (lateCancellationPenaltyPercent < 0 || lateCancellationPenaltyPercent > 100) {
            throw new IllegalArgumentException("Penalty percent must be between 0 and 100");
        }
        this.freeCancellationDaysNotice = freeCancellationDaysNotice;
        this.lateCancellationPenaltyPercent = lateCancellationPenaltyPercent;
    }

    public static CancellationPolicy standard() {
        return new CancellationPolicy(2, 50); // 2 days notice, 50% penalty for late cancellation
    }

    public double calculateRefundPercentage(LocalDate checkInDate, LocalDate cancellationDate) {
        Objects.requireNonNull(checkInDate, "Check-in date cannot be null");
        Objects.requireNonNull(cancellationDate, "Cancellation date cannot be null");

        if (cancellationDate.isAfter(checkInDate)) {
            return 0.0; // After check-in date -> 0% refund
        }

        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, checkInDate);
        if (daysUntilCheckIn >= freeCancellationDaysNotice) {
            return 100.0; // Full refund
        } else {
            return 100.0 - lateCancellationPenaltyPercent;
        }
    }

    @Override
    public String toString() {
        return "CancellationPolicy[" + freeCancellationDaysNotice + " days notice; " + lateCancellationPenaltyPercent + "% late fee]";
    }
}
