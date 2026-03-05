package app;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Player class that represents a player in
 * a Blackjack game with chip management and 
 * betting capabilities.
 * @author Maria Plascencia  
 */
public class Player implements Serializable{
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    private static final long serialVersionUID = 1L;
    private transient final int playerId;
    private final String name;
    private volatile double chipBalance;
    private Hand hand;
    private boolean isActive;
    private volatile boolean readyForAction;
    private volatile PlayerAction currentAction;

    /**
     * Enum for player actions.
     */
    public enum PlayerAction {
        HIT, STAND, WAITING, TIMEOUT
    }

    /**
     * Constructor for new player with initial chips.
     * @param playerId - unique identifier for the player
     * @param name - player's display name
     * @param initialChips - starting chip count
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Player(int playerId, String name, double initialChips) {
        if(playerId <= 0) {
            throw new IllegalArgumentException("Player ID must be a positive integer.");
        }
        if(name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty.");
        }
        if(initialChips < 0) {
            throw new IllegalArgumentException("Initial chips cannot be negative.");
        }

        this.playerId = playerId;
        this.name = name;
        this.chipBalance = initialChips;
        this.hand = null;
        this.isActive = true;
        this.readyForAction = false;
        this.currentAction = PlayerAction.WAITING;
    }

    /**
     * Place a bet for the current hand. 
     * @param amount - amount of chips to bet
     * @return true if bet is placed successfully, false otherwise
     */
    public boolean placeBet(double amount) {
        if(amount <= 0) {
            logError("Invalid bet amount: " + amount, null);
            return false;
        }
        double currentBalance = chipBalance;
        if(currentBalance < amount) {
            logError("Insufficient chips for bet.", null);
            return false;
        }

        try{
            if(hand == null) {
                hand = new Hand();
            }
            synchronized (this) {
                chipBalance -= amount;
                hand.setBet(amount);
            }
            logInfo("Player " + name + " placed a bet of " + amount);
            return true;
        } catch (Exception e) {
            logError("Error placing bet for player.", e);
            return false;
        }
    }

    /**
     * Add winnings to player's chip balance.
     * @param amount - amount of chips won
     */
    public void addWinnings(double amount) {
        if(amount < 0) {
            logError("Negative winnings amount: " + amount, null);
            return;
        }

        try {
            chipBalance += amount;
            logInfo("Player " + name + " won " + amount);
        } catch (Exception e) {
            logError("Error adding winnings for player.", e);
        }
    }

    /**
     * Add money to player's chip balance.
     * @param amount - amount to add
     */
    public void addMoney(double amount) {
        if(amount < 0) {
            logError("Negative amount to add: "+amount, null);
            return;
        }

        try {
            chipBalance += amount;
            logInfo("Player " + name + " added " + amount + " to balance.");
        } catch (Exception e) {
            logError("Error adding money for player.", e);
        }
    }

    /**
     * Player chooses to hit.
     */
    public void hit() {
        currentAction = PlayerAction.HIT;
        readyForAction = true;
        logInfo("Player " + name + " chooses to HIT");
    }

    /**
     * Player chooses to stand.
     */
    public void stand() {
        currentAction = PlayerAction.STAND;
        readyForAction = true;
        logInfo("Player " + name + " chooses to STAND");
    }

    /**
     * Set player timeout.
     */
    public void setTimeout() {
        currentAction = PlayerAction.TIMEOUT;
        readyForAction = true;
        logInfo("Player " + name + " timed out");
    }

    /**
     * Reset ready flag after action is processed.
     */
    public void resetReadyForAction() {
        readyForAction = false;
        currentAction = PlayerAction.WAITING;
    }

    /**
     * Check if player is ready for action.
     * @return true if player is ready
     */
    public boolean isReadyForAction() {
        return readyForAction;
    }

    /**
     * Get player's current action.
     * @return current player action
     */
    public PlayerAction getCurrentAction() {
        return currentAction;
    }

    /**
     * Get the current chip balance.
     * @return current chip balance
     */
    public double getChipBalance() {
        return chipBalance;
    }

    /**
     * Get the player's hand.
     * @return current hand or null if no hand exists
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Deal a card to the player's hand.
     * @param card - card to deal
     * @return true if successful, false otherwise
     */
    public boolean dealCard(Card card) {
        if (card == null) {
            logError("Cannot deal null card", null);
            return false;
        }
        
        if (hand == null) {
            logError("No active hand to deal card to - player must place bet first", null);
            return false;
        }
        hand.addCard(card);
        return true;
    }

    /**
     * Check if player has an active hand.
     * @return true if hand is active
     */
    public boolean hasActiveHand() {
        return hand != null && !hand.isBusted() && !hand.isStanding();
    }

    /**
     * Reset player's hand for a new round.
     */
    public void resetForNewRound() {
        hand = null;
        resetReadyForAction();
        logInfo("Player " + name + " reset for new round");
    }

    /**
     * Log error messages.
     * @param message - user-friendly error message
     * @param e - exception that occurred
     */
    private void logError(String message, Exception e) {
        try{
            if(e != null) {
                LOGGER.log(Level.SEVERE, message + " - Error details for debugging", e);
            } else {
                LOGGER.log(Level.SEVERE, message);
            }
        } catch (Exception loggingException) {
            System.err.println("An error occurred while logging: " + loggingException.getMessage());
        }
    }

    /**
     * Log informational messages.
     * @param message - info message
     */
    private void logInfo(String message) {
        try{
            LOGGER.info(message);
        } catch (Exception e) {
            System.err.println(message);
        }
    }

    /**
     * Get player ID.
     * @return player ID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Get player name.
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Check if player is active.
     * @return true if active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Set player active status.
     * @param active active status
     */
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return String.format("Player{id='%s', name='%s', chips=%f, active=%b, ready=%b, action=%s}", 
            playerId, name, chipBalance, isActive, readyForAction, currentAction);
    }
}