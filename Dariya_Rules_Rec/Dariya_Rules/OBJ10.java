// OBJ10-J. Do not use public static nonfinal fields
// implement maximum lives as a private static final constant 
package Dariya_Rules;

public class OBJ10 {
    // private static final field to ensure MAX_LIVES cannot be modified
    private static final int MAX_LIVES = 3;

    // public getter provides controlled read only access
    public static int getMaxLives() {
        return MAX_LIVES;
    }

    public static void main(String[] args) {
        System.out.println("Max lives per player: " + OBJ10.getMaxLives());
    }
}
