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
// SER11-J: Externable is never used
public class Player implements Serializable{

    //Declaring variables
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    private static final long serialVersionUID = 1L; //SER01-J: Explicit serialVersionUID for Serializable class
    private transient final String playerId; //SER03-J: Sensitive field not serialized
    private final String name;
    //VNA00-J: volatile variable for visibility across threads
    private volatile int chipBalance;
    private Hand hand; // Changed from List<Hand> to single Hand (removed splitting)
    private boolean isActive;
    
    // VNA00-J: Additional volatile for player action state (read by dealer thread)
    private volatile boolean readyForAction;
    private volatile PlayerAction currentAction;

    // Enum for player actions
    public enum PlayerAction {
        HIT, STAND, WAITING, TIMEOUT
    }

    /**
     * Constructor for new player with initial chips.
     * @param playerId - unique identifier for the player
     * @param name - player's display name
     * @param initialChips - starting chip count
     * @throws IllegalArgumentException if parameters are invalid (ERR07-J: specific exception)
     */
    public Player(String playerId, String name, int initialChips) {
        if(playerId == null || playerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Player ID cannot be null or empty.");
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
        this.hand = null; // No hand until bet is placed
        this.isActive = true;
        this.readyForAction = false;
        this.currentAction = PlayerAction.WAITING;
    }

    /**
     * Place a bet for the current hand. 
     * @param amount - amount of chips to bet
     * @return true if bet is placed successfully, false otherwise
     */
    public boolean placeBet(int amount) {
        //ERR08-J: Prevent NullPointerExceptions by checking parameters before use
        if(amount <= 0) {
            logError("Invalid bet amount: " + amount, null);
            return false;
        }
        int currentBalance = chipBalance; // Read volatile variable once for consistency
        if(currentBalance < amount) {
            logError("Insufficient chips for bet.", null);
            return false;
        }

        try{
            if(hand == null) {
                hand = new Hand();
            }
            //Atomic operation to update chip balance
            synchronized (this) {
                chipBalance -= amount;
                hand.setBet(amount);
            }

            logInfo("Player " + name + " placed a bet of " + amount);
            return true;
        } catch (Exception e) {
            //ERR01-J: Don't expose sensitive information in error messages, 
            //log the exception securely (ERR02-J)
            logError("Error placing bet for player.", e);
            return false;
        }
    }

    /**
     * Add winnings to player's chip balance.
     * @param amount - amount of chips won
     */
    public void addWinnings(int amount) {
        if(amount < 0) {
            logError("Negative winnings amount: " + amount, null);
            return;
        }

        try {
            chipBalance += amount;
            logInfo("Player " + name + " won " + amount);
        } catch (Exception e) {
            //ERR01-J: Don't expose sensitive information in error messages,
            //log the exception securely (ERR02-J)
            logError("Error adding winnings for player.", e);
        }
    }

    /**
     * Player chooses to hit
     * VNA00-J: volatile ensures action is visible to dealer thread
     */
    public void hit() {
        currentAction = PlayerAction.HIT;
        readyForAction = true;
        logInfo("Player " + name + " chooses to HIT");
    }

    /**
     * Player chooses to stand
     * VNA00-J: volatile ensures action is visible to dealer thread
     */
    public void stand() {
        currentAction = PlayerAction.STAND;
        readyForAction = true;
        logInfo("Player " + name + " chooses to STAND");
    }

    /**
     * Set player timeout (when they don't respond in time)
     */
    public void setTimeout() {
        currentAction = PlayerAction.TIMEOUT;
        readyForAction = true;
        logInfo("Player " + name + " timed out");
    }

    /**
     * Reset ready flag after action is processed
     */
    public void resetReadyForAction() {
        readyForAction = false;
        currentAction = PlayerAction.WAITING;
    }

    /**
     * Dealer checks if player is ready (will see most up-to-date value due to volatile)
     * @return true if player is ready
     */
    public boolean isReadyForAction() {
        return readyForAction;
    }

    /**
     * Dealer gets player's action (will see most up-to-date value due to volatile)
     * @return current player action
     */
    public PlayerAction getCurrentAction() {
        return currentAction;
    }

    /**
     * Get the current chip balance of the player.
     * @return - current chip balance
     */
    public int getChipBalance() {
        return chipBalance;
    }

    /**
     * Get the player's hand.
     * @return - current hand or null if no hand exists
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
     * Check if player has an active hand (not busted or standing)
     * @return true if hand is active
     */
    public boolean hasActiveHand() {
        return hand != null && !hand.isBusted() && !hand.isStanding();
    }

    /**
     * Reset player's hand for a new round (keep chips)
     */
    public void resetForNewRound() {
        hand = null;
        resetReadyForAction();
        logInfo("Player " + name + " reset for new round");
    }

    /**
     * Log error messages without exposing sensitive information (ERR01-J).
     * Handles logging exceptions securely (ERR02-J).
     * @param message - user-friendly error message
     * @param e - exception that occurred (can be null if not applicable)
     */
    private void logError(String message, Exception e) {
        try{
            if(e != null) {
                //Log detailed error for debugging, but avoid exposing sensitive info in the message (ERR01-J)
                LOGGER.log(Level.SEVERE, message + " - Error details for debugging", e);
            } else {
                LOGGER.log(Level.SEVERE, message);
            }
        } catch (Exception loggingException) {
            // Prevent exceptions while logging data
            System.err.println("An error occurred while logging: " + loggingException.getMessage());
        }
    }

    /**
     * Logs informational messages. (ERR02-J)
     * @param message - info message
     */
    private void logInfo(String message) {
        try{
            LOGGER.info(message);
        } catch (Exception e) {
            // Prevent exceptions while logging data
            System.err.println(message);
        }
    }

    //Getters and setters
    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String toString() {
        return String.format("Player{id='%s', name='%s', chips=%d, active=%b, ready=%b, action=%s}", 
            playerId, name, chipBalance, isActive, readyForAction, currentAction);
    }
}