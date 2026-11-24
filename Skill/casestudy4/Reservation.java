package casestudy4;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing a Hotel Reservation.
 * Enforces state machine invariants:
 * - REQUESTED -> CONFIRMED (requires PaymentGuarantee)
 * - CONFIRMED -> CHECKED_IN
 * - CHECKED_IN -> COMPLETED (Check-out)
 * - REQUESTED / CONFIRMED -> CANCELLED
 * - CHECKED_IN / COMPLETED cannot be cancelled (strictly illegal).
 * - Illegal states are unrepresentable!
 */
public class Reservation {
    private final ReservationId reservationId;
    private final Guest guest;
    private final RoomId roomId;
    private final DateRange stayPeriod;
    private final CancellationPolicy cancellationPolicy;
    private ReservationStatus status;
    private PaymentGuarantee paymentGuarantee;
    private double refundPercentIssued;

    public Reservation(ReservationId reservationId, Guest guest, RoomId roomId, DateRange stayPeriod, CancellationPolicy cancellationPolicy) {
        this.reservationId = Objects.requireNonNull(reservationId, "Reservation ID cannot be null");
        this.guest = Objects.requireNonNull(guest, "Guest cannot be null");
        this.roomId = Objects.requireNonNull(roomId, "Room ID cannot be null");
        this.stayPeriod = Objects.requireNonNull(stayPeriod, "Stay period cannot be null");
        this.cancellationPolicy = Objects.requireNonNull(cancellationPolicy, "Cancellation policy cannot be null");
        this.status = ReservationStatus.REQUESTED;
        this.refundPercentIssued = 0.0;
    }

    public ReservationId getReservationId() {
        return reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public RoomId getRoomId() {
        return roomId;
    }

    public DateRange getStayPeriod() {
        return stayPeriod;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public PaymentGuarantee getPaymentGuarantee() {
        return paymentGuarantee;
    }

    public double getRefundPercentIssued() {
        return refundPercentIssued;
    }

    public synchronized void confirm(PaymentGuarantee guarantee) {
        if (this.status != ReservationStatus.REQUESTED) {
            throw new IllegalStateException("Only REQUESTED reservations can be confirmed. Current status: " + status);
        }
        this.paymentGuarantee = Objects.requireNonNull(guarantee, "Cannot confirm reservation without payment guarantee!");
        this.status = ReservationStatus.CONFIRMED;
    }

    public synchronized void checkIn() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot check-in! Reservation must be in CONFIRMED status with payment guarantee. Current: " + status);
        }
        this.status = ReservationStatus.CHECKED_IN;
    }

    public synchronized void checkOut() {
        if (this.status != ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("Cannot check-out! Guest is not CHECKED_IN. Current: " + status);
        }
        this.status = ReservationStatus.COMPLETED;
    }

    public synchronized double cancel(LocalDate cancellationDate) {
        if (this.status == ReservationStatus.CHECKED_IN) {
            throw new IllegalStateException("CANCELLATION REJECTED: Guest has already checked in! No refund permitted after check-in.");
        }
        if (this.status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("CANCELLATION REJECTED: Reservation has already been completed.");
        }
        if (this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled.");
        }

        if (this.status == ReservationStatus.CONFIRMED) {
            this.refundPercentIssued = cancellationPolicy.calculateRefundPercentage(stayPeriod.getCheckIn(), cancellationDate);
        } else {
            // Requested reservation without payment guarantee has no charges
            this.refundPercentIssued = 0.0;
        }

        this.status = ReservationStatus.CANCELLED;
        return this.refundPercentIssued;
    }

    public boolean isActiveBooking() {
        return status == ReservationStatus.REQUESTED ||
                status == ReservationStatus.CONFIRMED ||
                status == ReservationStatus.CHECKED_IN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation that)) return false;
        return reservationId.equals(that.reservationId);
    }

    @Override
    public int hashCode() {
        return reservationId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Reservation[ID=%s, Room=%s, Guest=%s, Period=%s, Status=%s]",
                reservationId, roomId, guest.getName(), stayPeriod, status);
    }
}
