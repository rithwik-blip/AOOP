package casestudy1;

public class CaseStudy1Demo {
    public static void main(String[] args) {
        System.out.println("=== CASE STUDY 1: E-COMMERCE MONEY & PRICING DEMO ===");

        Product laptop = new Product("SKU-101", "Gaming Laptop",
                Money.of("1200.00", Currency.USD), Percentage.ofPercent(10));
        Product mouse = new Product("SKU-102", "Wireless Mouse",
                Money.of("25.50", Currency.USD), Percentage.ofBasisPoints(500)); // 5% discount

        System.out.println("Created Product 1: " + laptop + " -> Effective Price: " + laptop.getEffectivePrice());
        System.out.println("Created Product 2: " + mouse + " -> Effective Price: " + mouse.getEffectivePrice());

        Cart cart = new Cart("CART-999", Currency.USD);
        cart.addProduct(laptop, Quantity.of(1));
        cart.addProduct(mouse, Quantity.of(2));

        System.out.println("\n--- Cart Contents ---");
        for (Cart.CartItem item : cart.getItems()) {
            System.out.println("  " + item);
        }
        System.out.println("Calculated Cart Total: " + cart.calculateTotal());

        System.out.println("\n--- Demonstrating Invariant Enforcement ---");
        try {
            System.out.println("Attempting to create negative Money...");
            Money.of("-50.00", Currency.USD);
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully rejected: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to create 150% discount...");
            Percentage.ofPercent(150);
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully rejected: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to create Quantity of 0...");
            Quantity.of(0);
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully rejected: " + e.getMessage());
        }

        try {
            System.out.println("Attempting to add EUR product into USD cart...");
            Product eurBook = new Product("SKU-999", "Java Book", Money.of("45.00", Currency.EUR), Percentage.ofPercent(0));
            cart.addProduct(eurBook, Quantity.of(1));
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully rejected: " + e.getMessage());
        }
        System.out.println("=== Case Study 1 Verified Successfully! ===");
    }
}
