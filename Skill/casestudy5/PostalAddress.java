package casestudy5;

import java.util.Objects;

/**
 * Value Object representing a complete Postal Address.
 * Invariant: All constituent fields (street, city, postal code, country) are mandatory and non-blank.
 * Partial/incomplete postal addresses cannot be constructed.
 */
public final class PostalAddress {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public PostalAddress(String street, String city, String postalCode, String country) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("Street cannot be blank");
        if (city == null || city.isBlank()) throw new IllegalArgumentException("City cannot be blank");
        if (postalCode == null || postalCode.isBlank()) throw new IllegalArgumentException("Postal code cannot be blank");
        if (country == null || country.isBlank()) throw new IllegalArgumentException("Country cannot be blank");

        this.street = street.trim();
        this.city = city.trim();
        this.postalCode = postalCode.trim();
        this.country = country.trim();
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostalAddress that)) return false;
        return street.equals(that.street) &&
                city.equals(that.city) &&
                postalCode.equals(that.postalCode) &&
                country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country);
    }

    @Override
    public String toString() {
        return street + ", " + city + " " + postalCode + ", " + country;
    }
}
