package casestudy5;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a normalized Phone Number (e.g. E.164 format: +[country code][number]).
 * Invariant: Strips spaces/dashes and enforces international format.
 */
public final class PhoneNumber {
    private static final Pattern E164_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private final String e164Value;

    private PhoneNumber(String e164Value) {
        this.e164Value = e164Value;
    }

    public static PhoneNumber of(String rawPhone) {
        if (rawPhone == null || rawPhone.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be blank");
        }
        // Remove spaces, hyphens, and brackets
        String cleaned = rawPhone.replaceAll("[\\s\\-\\(\\)]", "");
        if (!E164_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Invalid phone number format: " + rawPhone + ". Must follow E.164 (e.g. +919876543210)");
        }
        return new PhoneNumber(cleaned);
    }

    public String getE164Value() {
        return e164Value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return e164Value.equals(that.e164Value);
    }

    @Override
    public int hashCode() {
        return e164Value.hashCode();
    }

    @Override
    public String toString() {
        return e164Value;
    }
}
