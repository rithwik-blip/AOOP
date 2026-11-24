package casestudy5;

import java.time.Instant;
import java.util.Objects;

/**
 * Algebraic / State representation of Email Verification.
 * Eliminates contradictory combinations of boolean flags and null timestamps.
 * Illegal states (e.g. verified=true with verifiedAt=null) are unrepresentable!
 */
public sealed interface EmailVerificationState permits
        EmailVerificationState.Unverified,
        EmailVerificationState.PendingVerification,
        EmailVerificationState.Verified {

    record Unverified() implements EmailVerificationState {}

    record PendingVerification(VerificationToken token) implements EmailVerificationState {
        public PendingVerification {
            Objects.requireNonNull(token, "Verification token cannot be null");
        }
    }

    record Verified(Instant verifiedAt) implements EmailVerificationState {
        public Verified {
            Objects.requireNonNull(verifiedAt, "Verified timestamp cannot be null");
        }
    }
}
