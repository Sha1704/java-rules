/**
 * Rule OBJ11-J. Be wary of letting constructors throw exceptions
 * 
 * Implement game acc age check and prevent underage player
 * 
 * @author Dariya
 */
package Dariya_Rules;

public final class OBJ11 {
    /**
     * constructor checks for underage players
     * throws SecurityException if the object cannot be safely constructed
     * 
     * @param age player's age
     */
    public OBJ11(int age){
        if(age<14){
            throw new SecurityException("Player is underage!");
        }
    }

    /**
     * method that can only be called on fully constructed objects
     */
    public void play(){
        System.out.println("Player is playing the game");
    }

    public static void main(String[] args){
        try{
            // constructor will throw exception, preventing partially initialize obj
            OBJ11 acc = new OBJ11(13);
            acc.play();
        } catch(SecurityException e) {
            System.out.println(e.getMessage());
        }
    }
}
