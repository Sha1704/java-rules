package app;
import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Implement VIP blackjack acc, inheriting from regular acc
// package-private (OBJ51) and final to prevent subclassing (OBJ11)
final class VIP extends Regular {
    // private field to control VIP boost and stores chosen perk (OBJ01)
    private boolean vipBoostActive;
    private String selectedPerk = "";

    // unmodifiable list of available VIP perks (OBJ10 & OBJ13)
    private static final List<String> AVAILABLE_PERKS = 
        Collections.unmodifiableList(Arrays.asList("CardCounting", "LoungeAccess"));

    // constructor fully initializes object (OBJ11)
    public VIP(String playerName, int balance, int gamesPlayed, Date lastLogin){
        super(playerName, balance, gamesPlayed, lastLogin);
        if(!isEligibleForVIP()){
            throw new IllegalStateException("Account is not eligible for VIP promotion");
        }
        this.vipBoostActive = true;
        System.out.println("\n You have been promoted to VIP!");
        System.out.println("Available VIP perks: " + AVAILABLE_PERKS);
    }

    // show available perks without allowing modification (OBJ13)
    public List<String> getAvailablePerks(){
        return AVAILABLE_PERKS;
    }

    // select one perk after VIP boost active (OBJ01)
    public void selectPerk(String perkChoice){
        if(!vipBoostActive){
            System.out.println("VIP boost not active yet! Play more games to unlock perks.");
            return;
        }
        if(AVAILABLE_PERKS.contains(perkChoice)){
            selectedPerk = perkChoice;
            System.out.println("Perk selected: " + selectedPerk);
        } else {
            System.out.println("Invalid perk! Choose available perks: " + AVAILABLE_PERKS);
        }
    }

    // selected perk method (OBJ01)
    public void usePerk(){
        if(selectedPerk.equals("CardCounting")){
            useCardCounting();
        } else if(selectedPerk.equals("LoungeAccess")){
            accessLounge();
        } else {
            System.out.println("No perk selected yet.");
        }
    }

    // private helper methods for perks
    private void useCardCounting(){
        System.out.println("Using card counting strategy!");
        // Azul card counting logic here
    }
    private void accessLounge(){
        System.out.println("Accessing VIP Lounge! Enjoy your perks!");
    }

    // determine if another acc is the same type by comparing classes directly (OBJ09)
    public boolean sameAccountType(VIP other){
        return this.getClass() == other.getClass();
    }
}
