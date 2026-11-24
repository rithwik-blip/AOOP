package casestudy4;

import java.util.Objects;

/**
 * Value Object representing a Verified Payment Guarantee.
 * Invariant: Cannot confirm a reservation without a valid payment guarantee reference.
 */
public final class PaymentGuarantee {
    private final String paymentMethod; // e.g., "CREDIT_CARD", "UPI", "CORPORATE_BILLING"
    private final String referenceNumber;
    private final long authorizedAmountMinor;

    public PaymentGuarantee(String paymentMethod, String referenceNumber, long authorizedAmountMinor) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method cannot be blank");
        }
        if (referenceNumber == null || referenceNumber.isBlank()) {
            throw new IllegalArgumentException("Reference number cannot be blank");
        }
        if (authorizedAmountMinor <= 0) {
            throw new IllegalArgumentException("Authorized amount must be positive");
        }
        this.paymentMethod = paymentMethod;
        this.referenceNumber = referenceNumber;
        this.authorizedAmountMinor = authorizedAmountMinor;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public long getAuthorizedAmountMinor() {
        return authorizedAmountMinor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PaymentGuarantee that)) return false;
        return authorizedAmountMinor == that.authorizedAmountMinor &&
                paymentMethod.equals(that.paymentMethod) &&
                referenceNumber.equals(that.referenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentMethod, referenceNumber, authorizedAmountMinor);
    }

    @Override
    public String toString() {
        return paymentMethod + ":" + referenceNumber + " (Auth: " + authorizedAmountMinor + ")";
    }
}
