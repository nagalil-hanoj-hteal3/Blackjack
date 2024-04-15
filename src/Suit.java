package src;

public enum Suit {
    CLUBS("C"), DIAMONDS("D"), HEARTS("H"), SPADES("S");

    private final String name;

    Suit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}