package casestudy5;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain Service managing cross-aggregate rules such as Email Uniqueness during user registration.
 */
public class UserRegistrationService {
    private final Map<EmailAddress, UserAccount> emailIndex = new HashMap<>();

    public synchronized UserAccount registerUser(String fullName, String rawEmail) {
        EmailAddress email = EmailAddress.of(rawEmail); // Normalizes case and validates RFC format

        if (emailIndex.containsKey(email)) {
            throw new IllegalStateException("Email address '" + email + "' is already registered to an existing account!");
        }

        UserAccount newAccount = new UserAccount(UserId.generate(), email, fullName);
        emailIndex.put(email, newAccount);
        return newAccount;
    }

    public synchronized void updateEmail(UserAccount account, String newRawEmail, Duration tokenValidity) {
        EmailAddress newEmail = EmailAddress.of(newRawEmail);
        if (emailIndex.containsKey(newEmail) && !emailIndex.get(newEmail).equals(account)) {
            throw new IllegalStateException("Email '" + newEmail + "' is already claimed by another account!");
        }
        emailIndex.remove(account.getEmail());
        account.changeEmail(newEmail, tokenValidity);
        emailIndex.put(newEmail, account);
    }
}
