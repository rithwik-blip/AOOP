package casestudy1;

import java.util.*;

/**
 * Aggregate Root representing a Shopping Cart.
 * Maintains consistency boundary: All items in cart must match cart currency,
 * total is computed exactly without rounding errors.
 */
public class Cart {
    private final String cartId;
    private final Currency currency;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public Cart(String cartId, Currency currency) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("Cart ID cannot be blank");
        }
        this.cartId = cartId;
        this.currency = Objects.requireNonNull(currency, "Cart currency cannot be null");
    }

    public String getCartId() {
        return cartId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void addProduct(Product product, Quantity quantity) {
        Objects.requireNonNull(product, "Product cannot be null");
        Objects.requireNonNull(quantity, "Quantity cannot be null");

        if (product.getBasePrice().getCurrency() != this.currency) {
            throw new IllegalArgumentException("Product currency (" + product.getBasePrice().getCurrency()
                    + ") does not match cart currency (" + this.currency + ")");
        }

        items.compute(product.getSku(), (sku, existingItem) -> {
            if (existingItem == null) {
                return new CartItem(product, quantity);
            } else {
                return existingItem.withAddedQuantity(quantity);
            }
        });
    }

    public void removeItem(String sku) {
        items.remove(sku);
    }

    public Collection<CartItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public Money calculateTotal() {
        Money total = Money.zero(currency);
        for (CartItem item : items.values()) {
            total = total.plus(item.getLineTotal());
        }
        return total;
    }

    public static final class CartItem {
        private final Product product;
        private final Quantity quantity;

        public CartItem(Product product, Quantity quantity) {
            this.product = Objects.requireNonNull(product, "Product cannot be null");
            this.quantity = Objects.requireNonNull(quantity, "Quantity cannot be null");
        }

        public Product getProduct() {
            return product;
        }

        public Quantity getQuantity() {
            return quantity;
        }

        public CartItem withAddedQuantity(Quantity additional) {
            return new CartItem(this.product, this.quantity.add(additional));
        }

        public Money getLineTotal() {
            return product.getEffectivePrice().times(quantity);
        }

        @Override
        public String toString() {
            return product.getName() + " x " + quantity + " = " + getLineTotal();
        }
    }
}
