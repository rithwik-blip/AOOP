package casestudy1;

import java.util.Objects;

/**
 * Entity representing a Product in the catalog.
 * Identified uniquely by SKU (Stock Keeping Unit).
 * Mutability: Name, base price, and discount percentage can evolve over time, but identity remains fixed.
 */
public class Product {
    private final String sku;
    private String name;
    private Money basePrice;
    private Percentage discount;

    public Product(String sku, String name, Money basePrice, Percentage discount) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("Product SKU cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        this.sku = sku;
        this.name = name;
        this.basePrice = Objects.requireNonNull(basePrice, "Base price cannot be null");
        this.discount = Objects.requireNonNull(discount, "Discount cannot be null");
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        this.name = newName;
    }

    public Money getBasePrice() {
        return basePrice;
    }

    public void updateBasePrice(Money newPrice) {
        this.basePrice = Objects.requireNonNull(newPrice, "Price cannot be null");
    }

    public Percentage getDiscount() {
        return discount;
    }

    public void updateDiscount(Percentage newDiscount) {
        this.discount = Objects.requireNonNull(newDiscount, "Discount cannot be null");
    }

    public Money getEffectivePrice() {
        return basePrice.minusPercentage(discount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return sku.equals(product.sku);
    }

    @Override
    public int hashCode() {
        return sku.hashCode();
    }

    @Override
    public String toString() {
        return "Product[SKU=" + sku + ", name=" + name + ", base=" + basePrice + ", disc=" + discount + "]";
    }
}
