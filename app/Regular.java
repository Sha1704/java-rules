package app;
import java.util.Date;

/**
 * Represents a regular blackjack account for player
 * 
 * Package-private class (OBJ51)
 */
class Regular {
    // private fields to ensure data safety (OBJ01, OBJ05)
    private final Date lastLogin;
    private String playerName;
    private int balance;
    private int gamesPlayed;

    /**
     * public constructor for new player acc
     * fully initializes obj and validates state (OBJ11)
     * 
     * @param playerName player name and cannot be null or blank
     * @param initialBalance starting balance and must be >=0
     */
    public Regular(String playerName, int initialBalance){
        if(playerName == null || playerName.isBlank()){
            throw new IllegalArgumentException("Player name cannot be blank");
        }
        if(initialBalance < 0){
            throw new IllegalArgumentException("Balance cannot be negative!");
        }
        this.playerName = playerName;
        this.balance = initialBalance;
        this.gamesPlayed = 0;
        this.lastLogin = new Date();
    }

    /**
     * package-private constructor for VIP promotion
     * 
     * @param playerName player name
     * @param balance current balance
     * @param gamesPlayed number of games played
     * @param lastLogin last login date
     */
    Regular(String playerName, int balance, int gamesPlayed, Date lastLogin){
        if(playerName == null || playerName.isBlank()){
            throw new IllegalArgumentException("Player name cannot be blank");
        }
        if(balance < 0){
            throw new IllegalArgumentException("Balance cannot be negative!");
        }
        if(gamesPlayed <0){
            throw new IllegalArgumentException("Games played cannot be negative!");
        }
        if(lastLogin == null){
            throw new IllegalArgumentException("Last login cannot be null");
        }
        this.playerName = playerName;
        this.balance = balance;
        this.gamesPlayed = gamesPlayed;
        this.lastLogin = (Date) lastLogin.clone();
    }

    /**
     * return defensive copy to protect mutable internal state (OBJ05)
     * 
     * @return copy of last login
     */
    public Date getLastLogin(){
        return (Date) lastLogin.clone();
    }

    /**
     * getters for immutable and primitive fields (OBJ01)
     * 
     * @return player's name
     */
    public String getPlayerName(){
        return playerName;
    }
    /**
     * 
     * @return current balance
     */
    public int getBalance(){
        return balance;
    }
    /**
     * 
     * @return number of games played
     */
    public int getGamesPlayed(){
        return gamesPlayed;
    }

    /**
     * play game method safely modifies internal state (OBJ01, OBJ11)
     * 
     * @param bet amount to bet and must be > 0 and <= balance
     */
    public void playGame(int bet){
        if(bet <=0){
            throw new IllegalArgumentException("Bet must be greater than 0!");
        }
        // subtract bet from balance if enough funds
        if(bet <= balance){
            balance -= bet;
        } else {
            throw new IllegalArgumentException("Insufficient Balance");
        }
        // track game played
        gamesPlayed++;
    }

    /**
     * check if regular acc eligible for VIP promotion
     * 
     * @return true if gamesPlayed >= 10
     */
    public boolean isEligibleForVIP(){
        return gamesPlayed >= 10;
    }

    /**
     * check whether another acc is the same type by comparing classes directly (OBJ09)
     * @param other another regular account
     * @return true if both objects are of the same class
     */
    public boolean sameAccountType(Regular other){
        return this.getClass() == other.getClass();
    }
}
