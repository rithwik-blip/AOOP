package casestudy5;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Objects;

/**
 * Value Object representing a cryptographically secure verification token.
 * Invariant: Contains token string and expiration timestamp. Knows how to evaluate expiry.
 */
public final class VerificationToken {
    private final String token;
    private final Instant expiresAt;

    private VerificationToken(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }

    public static VerificationToken generate(Duration validity) {
        Objects.requireNonNull(validity, "Validity duration cannot be null");
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String tokenStr = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return new VerificationToken(tokenStr, Instant.now().plus(validity));
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(Instant currentInstant) {
        Objects.requireNonNull(currentInstant, "Current instant cannot be null");
        return currentInstant.isAfter(expiresAt);
    }

    public boolean matches(String candidateToken) {
        return this.token.equals(candidateToken);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerificationToken that)) return false;
        return token.equals(that.token) && expiresAt.equals(that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expiresAt);
    }

    @Override
    public String toString() {
        return "Token[expiresAt=" + expiresAt + "]";
    }
}
