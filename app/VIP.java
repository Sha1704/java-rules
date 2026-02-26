package app;
// Implement VIP blackjack acc, inheriting from regular acc
// VIP acc get special features like a boost after 10 games

// package-private to reduce unnecessary exposure (OBJ51)
class VIP extends Regular {
    // private field to control VIP boost, limits accessibility (OBJ01)
    private boolean vipBoostActive;

    // constructor fully initializes object (OBJ11)
    public VIP(String playerName, int initialBalance){
        super(playerName, initialBalance);
        // vip boost starts inactive
        this.vipBoostActive = false;
    }

    // public accessor for VIP boost without exposing mutable object (OBJ05, OBJ13)
    public boolean isVipBoostActive(){
        return vipBoostActive;
    }

    // activates VIP boost automatically after player has played more than 10 games 
    // accesses inherited private field via getter (OBJ05)
    public void checkAndActivateVIPBoost(){
        // gamesPlayed is inherited from regular acc
        if(getGamesPlayed() >= 10){
            vipBoostActive = true;
        }
    }

    // only works if VIP boost is active
    public void useCardCounting(){
        if(vipBoostActive){
            // Azul card counting logic call here
        }
    }

    // determine if another acc is the same type by comparing classes directly (OBJ09)
    // no overloaded, clear single purpose method (OBJ10)
    public boolean sameAccountType(VIP other){
        return this.getClass() == other.getClass();
    }
}
