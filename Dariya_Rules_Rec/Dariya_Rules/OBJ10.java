/**
 * Rule OBJ10-J. Do not use public static nonfinal fields
 * 
 * implement maximum lives as a private static final constant,
 * provding controlled read only access through a public getter
 * 
 * @author Dariya
 */
package Dariya_Rules;

public class OBJ10 {
    // private static final field to ensure MAX_LIVES cannot be modified
    private static final int MAX_LIVES = 3;

    /**
     * Returns the max num of lives per player
     * 
     * @return maximum lives
     */
    public static int getMaxLives() {
        return MAX_LIVES;
    }

    public static void main(String[] args) {
        System.out.println("Max lives per player: " + OBJ10.getMaxLives());
    }
}
