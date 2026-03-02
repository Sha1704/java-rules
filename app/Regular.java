package app;
import java.util.Date;

// Implement Regular blackjack account for player
// Package-private class (OBJ51)
class Regular {
    // private fields to ensure data safety (OBJ01, OBJ05)
    private final Date lastLogin;
    private String playerName;
    private int balance;
    private int gamesPlayed;

    // constructor fully initializes obj and validates state (OBJ11)
    public Regular(String playerName, int initialBalance){
        if(initialBalance < 0){
            throw new IllegalArgumentException("Balance cannot be negative!");
        }
        this.playerName = playerName;
        this.balance = initialBalance;
        this.gamesPlayed = 0;
        this.lastLogin = new Date();
    }

    // package-private constructor for VIP promotion
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

    // return defensive copy to protect mutable internal state (OBJ05)
    public Date getLastLogin(){
        return (Date) lastLogin.clone();
    }

    // getters for immutable and primitive fields (OBJ01)
    public String getPlayerName(){
        return playerName;
    }
    public int getBalance(){
        return balance;
    }
    public int getGamesPlayed(){
        return gamesPlayed;
    }

    // play game method safely modifies internal state (OBJ01, OBJ11)
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

    // check if regular acc eligible for VIP promotion
    public boolean isEligibleForVIP(){
        return gamesPlayed >= 10;
    }

    // determines whether another acc is the same type by comparing classes directly (OBJ09)
    public boolean sameAccountType(Regular other){
        return this.getClass() == other.getClass();
    }
}
