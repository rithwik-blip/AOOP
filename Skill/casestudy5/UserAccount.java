package casestudy5;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate Root representing a User Account.
 * Invariants Enforced:
 * 1. Email is strongly typed (EmailAddress) and normalized.
 * 2. Email verification state is an algebraic sealed hierarchy:
 *    - Unverified
 *    - PendingVerification(token)
 *    - Verified(verifiedAt)
 * 3. Expired tokens are rejected; verified status cannot exist without a non-null timestamp.
 * 4. Suspended and deleted accounts are barred from logging in or mutating state.
 * 5. Optional phone and address use java.util.Optional.
 */
public class UserAccount {
    private final UserId userId;
    private EmailAddress email;
    private String fullName;
    private EmailVerificationState verificationState;
    private AccountStatus status;
    private PhoneNumber phoneNumber;       // Optional
    private PostalAddress postalAddress;   // Optional

    public UserAccount(UserId userId, EmailAddress email, String fullName) {
        this.userId = Objects.requireNonNull(userId, "UserId cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be blank");
        }
        this.fullName = fullName.trim();
        this.verificationState = new EmailVerificationState.Unverified();
        this.status = AccountStatus.ACTIVE;
    }

    public UserId getUserId() {
        return userId;
    }

    public synchronized EmailAddress getEmail() {
        return email;
    }

    public synchronized String getFullName() {
        return fullName;
    }

    public synchronized EmailVerificationState getVerificationState() {
        return verificationState;
    }

    public synchronized AccountStatus getStatus() {
        return status;
    }

    public synchronized Optional<PhoneNumber> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public synchronized void setPhoneNumber(PhoneNumber phoneNumber) {
        requireOperationalAccount();
        this.phoneNumber = phoneNumber;
    }

    public synchronized Optional<PostalAddress> getPostalAddress() {
        return Optional.ofNullable(postalAddress);
    }

    public synchronized void setPostalAddress(PostalAddress postalAddress) {
        requireOperationalAccount();
        this.postalAddress = postalAddress;
    }

    public synchronized VerificationToken initiateEmailVerification(Duration tokenValidity) {
        requireOperationalAccount();
        VerificationToken token = VerificationToken.generate(tokenValidity);
        this.verificationState = new EmailVerificationState.PendingVerification(token);
        return token;
    }

    public synchronized void confirmEmailVerification(String candidateToken, Instant now) {
        requireOperationalAccount();
        if (!(verificationState instanceof EmailVerificationState.PendingVerification pending)) {
            throw new IllegalStateException("No verification pending for this account.");
        }

        if (pending.token().isExpired(now)) {
            throw new IllegalStateException("Verification token has expired at " + pending.token().getExpiresAt() + ". Please request a new token.");
        }

        if (!pending.token().matches(candidateToken)) {
            throw new IllegalArgumentException("Invalid verification token provided.");
        }

        // Successfully verified!
        this.verificationState = new EmailVerificationState.Verified(now);
    }

    public synchronized VerificationToken changeEmail(EmailAddress newEmail, Duration tokenValidity) {
        requireOperationalAccount();
        Objects.requireNonNull(newEmail, "New email cannot be null");
        if (this.email.equals(newEmail)) {
            throw new IllegalArgumentException("New email is identical to current email.");
        }
        this.email = newEmail;
        // Invariant: Changing email resets verification state to PendingVerification!
        VerificationToken token = VerificationToken.generate(tokenValidity);
        this.verificationState = new EmailVerificationState.PendingVerification(token);
        return token;
    }

    public synchronized void suspend() {
        if (this.status == AccountStatus.DELETED) {
            throw new IllegalStateException("Cannot suspend a deleted account.");
        }
        this.status = AccountStatus.SUSPENDED;
    }

    public synchronized void reactivate() {
        if (this.status == AccountStatus.DELETED) {
            throw new IllegalStateException("Cannot reactivate a deleted account.");
        }
        this.status = AccountStatus.ACTIVE;
    }

    public synchronized void delete() {
        this.status = AccountStatus.DELETED;
    }

    public synchronized boolean canLogin() {
        if (status == AccountStatus.SUSPENDED) {
            return false;
        }
        if (status == AccountStatus.DELETED) {
            return false;
        }
        // System policy: user must have active status and verified email to log in
        return verificationState instanceof EmailVerificationState.Verified;
    }

    private void requireOperationalAccount() {
        if (status == AccountStatus.SUSPENDED) {
            throw new IllegalStateException("Operation rejected: Account " + userId + " is SUSPENDED.");
        }
        if (status == AccountStatus.DELETED) {
            throw new IllegalStateException("Operation rejected: Account " + userId + " is DELETED.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserAccount[ID=%s, Email=%s, Status=%s, Verification=%s]",
                userId, email, status, verificationState.getClass().getSimpleName());
    }
}
