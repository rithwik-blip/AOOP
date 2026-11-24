package casestudy5;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a normalized, valid Email Address.
 * Invariants:
 * - Must adhere to standard email syntax (RFC 5322 regex).
 * - Automatically normalized to lower-case to prevent duplicate registration under different casing.
 */
public final class EmailAddress {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}$"
    );

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress of(String rawEmail) {
        if (rawEmail == null || rawEmail.isBlank()) {
            throw new IllegalArgumentException("Email address cannot be blank");
        }
        String trimmed = rawEmail.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Invalid email address format: " + rawEmail);
        }
        // Normalize to lower case to eliminate case variations
        return new EmailAddress(trimmed.toLowerCase());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailAddress that)) return false;
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
