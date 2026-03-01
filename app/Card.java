package app;

import java.util.Arrays;

public class Card {

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(11), QUEEN(12), KING(13), ACE(1);

        private final int value;
        
        Rank(int value) { 
            this.value = value; 
        }

        public int getValue() { return value; }
    }

    private final Rank rank;
    private final Suit suit;

    // Constructor
    public Card(Rank rank, Suit suit) {
        if (rank == null || suit == null) {
            throw new IllegalArgumentException("Rank and Suit cannot be null");
        }
        this.rank = rank;
        this.suit = suit;
    }

    //getters
    public Suit getSuit() { 
        return suit; 
    }

    public Rank getRank() { 
        return rank; 
    }

    public int getValue() { 
        return rank.getValue(); 
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    // EXP02-J: Compare arrays correctly
    public static boolean compareCards(Card[] a, Card[] b) {
        return Arrays.equals(a, b);
    }

    //OBJ14-J: Do not use an object that has been freed.
    static class Resource {
        private boolean open = true;

        public void use() {
            if (!open) {
                throw new IllegalStateException("Resource has been freed");
            }
            System.out.println("Resource in use");
        }

        public void free() {
            open = false;
            System.out.println("Resource freed");
        }
    }
}