package casestudy1;

public enum Currency {
    EUR(2),
    USD(2),
    JPY(0),
    KWD(3),
    INR(2);

    private final int minorUnits;

    Currency(int minorUnits) {
        this.minorUnits = minorUnits;
    }

    public int getMinorUnits() {
        return minorUnits;
    }
}
