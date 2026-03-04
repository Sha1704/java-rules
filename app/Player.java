package app;

// SER11-J: If Externalizable is ever used, add a guard in readExternal()
// to prevent multiple initialization (e.g., boolean initialized flag).
// For Serializable, readObject is called only once automatically, so no guard needed.
// ^^ Ask me if that makes no sense its a note for me later when this is in -Carlos 2/25
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

 /**
  * Player class that represents a player in
  * a Blackjack game with chip management and 
  * betting capabilities.
  * @author Maria Plascencia  
  */
public class Player {

    //Declaring variables
    private static final Logger LOGGER = Logger.getLogger(Player.class.getName());
    private final int playerId;
    private final String name;
    //VNA00-J: volatile variable for visibility across threads
    private volatile double chipBalance; //changed to double
    private final List<Hand> hands;
    private boolean isActive;

    /**
     * Constructor for new player with initial chips.
     * @param playerId - unique identifier for the player
     * @param name - player's display name
     * @param initialChips - starting chip count
     * @throws IllegalArgumentException if parameters are invalid (ERRo7-J: specific exception)
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
        //Thread safe list
        this.hands = Collections.synchronizedList(new ArrayList<>());
        this.isActive = true;
    }

    /**
     * Place a bet for the current hand. 
     * @param amount - amount of chips to bet
     * @return true if bet is placed successfully, false otherwise
     */
    public boolean placeBet(double amount) {
        //ERR08-J: Prevent NullPointerExceptions by checking parameters before use
       if(amount <= 0) {
            logError("Invalid bet amount: " + amount, null);
            return false;
        }
        double currentBalance = chipBalance; // Read volatile variable once for consistency
        if(currentBalance < amount) {
            logError("Insufficient chips for bet."  , null);
            return false;
        }

        try{
            Hand currentHand = getCurrentHand();
            if(currentHand == null) {
                currentHand = new Hand();
                hands.add(currentHand);
            }
            //Atomic operation to update chip balance
            synchronized (this) {
                chipBalance -= amount;
                currentHand.setBet(amount);
            }

            logInfo("Player " + name + " placed a bet of " + amount);
            return true;
        } catch (Exception e) {
            //ERR01-J:Don't expose sensitive information in error messages, 
            //log the exception securely (ERR02-J)
            logError("Error placing bet for player.", e);
            return false;
        }
        
    }

    /**
     * Add winings to player's chip balance.
     * @param amount - amount of chips won
     */
    public void addWinnings(double amount) {
        if(amount < 0) {
            logError("Negative winnings amount: "+amount, null);
            return;
        }

        try {
            chipBalance += amount; // Atomic operation to update chip balance
            logInfo("Player " + name + " won " + amount);
        } catch (Exception e) {
            //ERR01-J:Don't expose sensitive information in error messages,
            //log the exception securely (ERR02-J)
            logError("Error adding winnings for player.", e);
        }
    }
    public void addMoney(double amount) {
        if(amount < 0) {
            logError("Negative amount to add: "+amount, null);
            return;
        }

        try {
            chipBalance += amount; // Atomic operation to update chip balance
            logInfo("Player " + name + " added " + amount + " to balance.");
        } catch (Exception e) {
            //ERR01-J:Don't expose sensitive information in error messages,
            //log the exception securely (ERR02-J)
            logError("Error adding money for player.", e);
        }
    }

    /**
     * Get the current chip balance of the player.
     * @return - current chip balance
     */
    public double getChipBalance() {
        return chipBalance;
    }

    /**
     * Get the current hand of the player.
     * @return - current hand or null if no hands are available
     */
    private Hand getCurrentHand() {
        //ERR08-J: Prevent NullPointerExceptions 
        if(hands.isEmpty()) {
            return null;
        }
        return hands.get(hands.size() - 1);
    }

    /**
     * Log error messages without exposing sensitive information (ERR01-J).
     * Handles logging exceptions securely (ERR02-J).
     * @param message - user-friendly error message
     * @param e - exception that ocurred (can be null if not applicable)
     */
    private void logError(String message, Exception e) {
        try{
            if(e != null) {
                //Log detailed error for debugging, but avoid exposing sensitive info in the message (ERR01-J)
                LOGGER.log(Level.SEVERE, message+"-Error details fr debugging", e);
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
        } catch (Exception e)
        {
            // Prevent exceptions while logging data
            System.err.println(message);
        }
    }

    //Getters and setters
    public int getPlayerId() {
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

    public List<Hand> getHands() {
        //MET52-J: Return a defensive copy of the list 
        synchronized (hands) {
            return new ArrayList<>(hands);
        }
    }

    @Override
    public String toString() {
        return String.format("Player{id='%s', name='%s', chips=%d, active=%b}", 
        playerId, name, chipBalance, isActive);
    }
     
}
