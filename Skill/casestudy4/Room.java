package casestudy4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root representing a Hotel Room and its booking schedule.
 * Consistency Boundary: Enforces invariant that a room can NEVER be booked for overlapping date ranges.
 */
public class Room {
    private final RoomId roomId;
    private final String roomType; // e.g. "Deluxe Suite"
    private final List<Reservation> activeReservations = new ArrayList<>();

    public Room(RoomId roomId, String roomType) {
        this.roomId = Objects.requireNonNull(roomId, "Room ID cannot be null");
        if (roomType == null || roomType.isBlank()) {
            throw new IllegalArgumentException("Room type cannot be blank");
        }
        this.roomType = roomType;
    }

    public RoomId getRoomId() {
        return roomId;
    }

    public String getRoomType() {
        return roomType;
    }

    public synchronized boolean isAvailable(DateRange requestedPeriod) {
        Objects.requireNonNull(requestedPeriod, "Requested period cannot be null");
        for (Reservation res : activeReservations) {
            if (res.isActiveBooking() && res.getStayPeriod().overlapsWith(requestedPeriod)) {
                return false;
            }
        }
        return true;
    }

    public synchronized Reservation book(ReservationId resId, Guest guest, DateRange stayPeriod, CancellationPolicy policy) {
        Objects.requireNonNull(stayPeriod, "Stay period cannot be null");
        if (!isAvailable(stayPeriod)) {
            throw new IllegalStateException("Double booking conflict! Room " + roomId + " is already booked for period: " + stayPeriod);
        }

        Reservation reservation = new Reservation(resId, guest, roomId, stayPeriod, policy);
        activeReservations.add(reservation);
        return reservation;
    }

    public synchronized List<Reservation> getReservations() {
        return Collections.unmodifiableList(new ArrayList<>(activeReservations));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return roomId.equals(room.roomId);
    }

    @Override
    public int hashCode() {
        return roomId.hashCode();
    }

    @Override
    public String toString() {
        return "Room[" + roomId + ", Type=" + roomType + "]";
    }
}
