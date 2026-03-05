package app;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * Hand class that represents a hand of cards in a Blackjack 
 * game and a bet amount.
 * @author Maria Plascencia  
 */
public class Hand implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Hand.class.getName());
    private final List<Card> cards;
    private double bet;
    private boolean isStanding;
    private boolean isBusted;

    /**
     * Constructor for a new hand with no cards and zero bet.
     */
    public Hand() {
        this.cards = new ArrayList<>();
        this.bet = 0;
        this.isStanding = false;
        this.isBusted = false;
    }

    /**
     * Add a card to the hand.
     * MET51-J: Single addCard method for Card objects - no overloading
     * to differentiate between runtime types
     * @param card - card to be added to the hand
     */
    public void addCard(Card card) {
        if (card == null) {
            return;
        }
        cards.add(card);
    }

    /**
     * Get the total value of the hand, accounting for Aces as 1 or 11.
     * @return - total value of the hand
     */
    public int getValue(){
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            if(card == null) continue;

            if(card.getRank() == Card.Rank.ACE) {
                aces++;
            }
            value += card.getValue();
        }
        while(value > 21 && aces > 0) {
                value -= 10;
                aces--;
        }
        return value;
    }

    /**
     * Check if the hand is a Blackjack (two cards totaling 21).
     * @return - true if the hand is a Blackjack, false otherwise
     */
    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    /**
     * Set bet amount for the hand.
     * @param bet - amount of chips to bet on this hand
     */
    public void setBet(double bet) {
        if (bet < 0) {
            return;
        }
        this.bet =  bet;
    }

    /**
     * Get the current bet amount for the hand.
     * @return - current bet amount
     */
    public double getBet() {
        return bet;
    }

    /**
     * Get if the hand is standing (player has chosen to stand).
     * @return - true if the hand is standing, false otherwise
     */
    public boolean isStanding() {
        return isStanding;
    }

    /**
     * Set standing status for the hand.
     * @param standing - true if the hand is standing, false otherwise
     */
    public void setStanding(boolean standing) {
        isStanding = standing;
    }

    /**
     * Get if the hand is busted (total value exceeds 21).
     * @return - true if the hand is busted, false otherwise
     */
    public boolean isBusted() {
        return isBusted;
    }

    /**
     * Set busted status for the hand.
     * @param busted - true if the hand is busted, false otherwise
     */
    public void setBusted(boolean busted) {
        isBusted = busted;
    }

    /**
     * Get a list of cards in the hand.
     * @return - list of cards in the hand
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}