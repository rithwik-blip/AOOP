package casestudy5;

import java.time.Duration;
import java.time.Instant;

public class CaseStudy5Demo {
    public static void main(String[] args) {
        System.out.println("=== CASE STUDY 5: USER ACCOUNTS & EMAIL VERIFICATION DEMO ===");

        UserRegistrationService regService = new UserRegistrationService();

        // 1. Register User with upper/mixed case email -> Normalized automatically
        System.out.println("Registering user with 'John.Doe@EXAMPLE.Com'...");
        UserAccount user1 = regService.registerUser("John Doe", "John.Doe@EXAMPLE.Com");
        System.out.println("Created User: " + user1);
        System.out.println("Normalized Email: " + user1.getEmail());

        // 2. Prevent duplicate registration with different casing
        System.out.println("\n--- Testing Case-Insensitive Email Duplicate Rejection ---");
        try {
            System.out.println("Attempting duplicate registration with 'john.doe@example.com'...");
            regService.registerUser("John Clone", "john.doe@example.com");
        } catch (IllegalStateException e) {
            System.out.println("Successfully blocked duplicate registration: " + e.getMessage());
        }

        // 3. Email Verification Flow
        System.out.println("\n--- Testing Email Verification Flow ---");
        Duration validity = Duration.ofMinutes(15);
        VerificationToken token = user1.initiateEmailVerification(validity);
        System.out.println("Initiated email verification. Generated token: " + token);
        System.out.println("Current verification state: " + user1.getVerificationState());
        System.out.println("Can user log in before verification? " + user1.canLogin());

        // Attempt verification with expired time
        System.out.println("\nAttempting verification with simulated expired time (+20 minutes)...");
        try {
            user1.confirmEmailVerification(token.getToken(), Instant.now().plus(Duration.ofMinutes(20)));
        } catch (IllegalStateException e) {
            System.out.println("Successfully rejected expired token: " + e.getMessage());
        }

        // Legitimate verification
        System.out.println("\nVerifying with valid token and current time...");
        user1.confirmEmailVerification(token.getToken(), Instant.now());
        System.out.println("Verification State: " + user1.getVerificationState());
        System.out.println("Can user log in now? " + user1.canLogin());

        // 4. Contact Details (Phone & Address Value Objects)
        System.out.println("\n--- Testing Contact Details Value Objects ---");
        PhoneNumber phone = PhoneNumber.of("+91 98765-43210");
        PostalAddress address = new PostalAddress("42 MG Road", "Bengaluru", "560001", "India");
        user1.setPhoneNumber(phone);
        user1.setPostalAddress(address);
        System.out.println("Phone (E.164 normalized): " + user1.getPhoneNumber().get());
        System.out.println("Address: " + user1.getPostalAddress().get());

        // 5. Account Suspension Protection
        System.out.println("\n--- Testing Account Suspension ---");
        user1.suspend();
        System.out.println("Account status after suspension: " + user1.getStatus());
        System.out.println("Can suspended user log in? " + user1.canLogin());
        try {
            System.out.println("Attempting to update phone number while suspended...");
            user1.setPhoneNumber(PhoneNumber.of("+14155552671"));
        } catch (IllegalStateException e) {
            System.out.println("Successfully blocked operation on suspended account: " + e.getMessage());
        }

        System.out.println("\n=== Case Study 5 Verified Successfully! ===");
    }
}
