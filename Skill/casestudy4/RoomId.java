package casestudy4;

import java.util.Objects;

/**
 * Value Object representing a Room Identifier (e.g. "ROOM-101").
 */
public final class RoomId {
    private final String value;

    public RoomId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Room ID cannot be blank");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomId roomId)) return false;
        return value.equals(roomId.value);
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
