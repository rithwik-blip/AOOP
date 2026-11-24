package casestudy5;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a Unique User Identifier.
 */
public final class UserId {
    private final String value;

    public UserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be blank");
        }
        this.value = value.trim();
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;
        return value.equals(userId.value);
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
