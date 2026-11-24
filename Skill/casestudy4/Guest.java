package casestudy4;

import java.util.Objects;

/**
 * Entity representing a Hotel Guest.
 */
public class Guest {
    private final String guestId;
    private String name;
    private String email;
    private String phone;

    public Guest(String guestId, String name, String email, String phone) {
        if (guestId == null || guestId.isBlank()) throw new IllegalArgumentException("Guest ID cannot be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Guest name cannot be blank");
        this.guestId = guestId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getGuestId() {
        return guestId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Guest guest)) return false;
        return guestId.equals(guest.guestId);
    }

    @Override
    public int hashCode() {
        return guestId.hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + guestId + ")";
    }
}
