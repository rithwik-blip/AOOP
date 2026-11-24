package casestudy4;

import java.time.LocalDate;

public class CaseStudy4Demo {
    public static void main(String[] args) {
        System.out.println("=== CASE STUDY 4: HOTEL RESERVATIONS & DATE RANGES DEMO ===");

        RoomId roomId = new RoomId("ROOM-305");
        Room deluxeRoom = new Room(roomId, "Executive Deluxe Suite");
        CancellationPolicy policy = CancellationPolicy.standard();

        Guest guest1 = new Guest("GUEST-1", "Rajesh Kumar", "rajesh@example.com", "9876543210");
        Guest guest2 = new Guest("GUEST-2", "Priya Sharma", "priya@example.com", "9123456780");

        // 1. Create valid stay period: Dec 10 to Dec 15
        LocalDate d10 = LocalDate.of(2026, 12, 10);
        LocalDate d15 = LocalDate.of(2026, 12, 15);
        DateRange stay1 = DateRange.of(d10, d15);
        System.out.println("Created Stay 1: " + stay1);

        // Book guest 1
        Reservation res1 = deluxeRoom.book(new ReservationId("RES-001"), guest1, stay1, policy);
        System.out.println("Created Reservation 1: " + res1);

        // Confirm with payment guarantee
        PaymentGuarantee guarantee1 = new PaymentGuarantee("CREDIT_CARD", "AUTH_998877", 2500000L);
        res1.confirm(guarantee1);
        System.out.println("Confirmed Reservation 1 with Payment Guarantee: " + res1.getPaymentGuarantee());

        // 2. Test Consecutive booking (Check-in on Dec 15 when guest 1 checks out on Dec 15)
        LocalDate d20 = LocalDate.of(2026, 12, 20);
        DateRange stayConsecutive = DateRange.of(d15, d20);
        System.out.println("\nTesting consecutive stay (Dec 15 to Dec 20):");
        System.out.println("Is room available for consecutive booking? " + deluxeRoom.isAvailable(stayConsecutive));
        Reservation resConsecutive = deluxeRoom.book(new ReservationId("RES-002"), guest2, stayConsecutive, policy);
        System.out.println("Successfully booked consecutive reservation: " + resConsecutive);

        // 3. Test Overlapping booking rejection (Double Booking Prevention)
        System.out.println("\n--- Testing Double Booking Prevention ---");
        DateRange overlappingStay = DateRange.of(LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 14));
        try {
            System.out.println("Attempting overlapping booking (Dec 12 to Dec 14)...");
            deluxeRoom.book(new ReservationId("RES-003"), guest2, overlappingStay, policy);
        } catch (IllegalStateException e) {
            System.out.println("Successfully blocked double booking: " + e.getMessage());
        }

        // 4. Test Invalid Date Range (Check-out before Check-in)
        System.out.println("\n--- Testing Invalid DateRange Creation ---");
        try {
            System.out.println("Attempting check-out earlier than check-in...");
            DateRange.of(LocalDate.of(2026, 12, 15), LocalDate.of(2026, 12, 10));
        } catch (IllegalArgumentException e) {
            System.out.println("Successfully blocked invalid date range: " + e.getMessage());
        }

        // 5. Test Check-in and Cancellation after Check-in rejection
        System.out.println("\n--- Testing Cancellation Rules ---");
        res1.checkIn();
        System.out.println("Guest 1 status after check-in: " + res1.getStatus());
        try {
            System.out.println("Attempting to cancel reservation after check-in...");
            res1.cancel(LocalDate.of(2026, 12, 11));
        } catch (IllegalStateException e) {
            System.out.println("Successfully blocked post-check-in cancellation: " + e.getMessage());
        }

        System.out.println("\n=== Case Study 4 Verified Successfully! ===");
    }
}
