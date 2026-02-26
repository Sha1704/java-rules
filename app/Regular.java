package app;
// Implement Regular blackjack account for player
// Package-private class (OBJ51)
class Regular {
    // private fields to ensure data safety (OBJ01)
    private String playerName;
    private int balance;
    private int gamesPlayed;

    // ensures all fields have valid initial values (OBJ11)
    public Regular(String playerName, int initialBalance){
        this.playerName = playerName;
        this.balance = initialBalance;
        this.gamesPlayed = 0;
    }

    // access to fields without exposing internal data (OBJ05 & OBJ13)
    public String getPlayerName(){
        return playerName;
    }
    public int getBalance(){
        return balance;
    }
    public int getGamesPlayed(){
        return gamesPlayed;
    }

    // play game method
    // changes balance and gamesPlayed safely without exposing internal state (OBJ05)
    public void playGame(int bet){
        // subtract bet from balance if enough funds
        if(bet <= balance){
            balance -= bet;
        } else {
            throw new IllegalArgumentException("Insufficient Balance");
        }
        // track game played
        gamesPlayed++;
    }

    // determines whether another acc is the same type by comparing classes directly (OBJ09)
    // no overloaded, clear single purpose method (OBJ10)
    public boolean sameAccountType(Regular other){
        return this.getClass() == other.getClass();
    }
}
