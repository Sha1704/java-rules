/**
 * Rule OBJ51-J. Minimize the accessibility of classes and their members.
 * 
 * Implement player stats with minimal accessibility.
 *
 * @author Dariya
 */
package Dariya_Rec;

final class stats{
    private int score;
    private int lives;

    /**
     * package-private constructor
     * Creates a stats obj with a specified num of lives
     * 
     * @param initialLives starting number of lives
     */
    stats(int initialLives){
        this.score = 0;
        this.lives = initialLives;
    }

    /**
     * Adds points to the player's score
     * 
     * @param points number of points to add
     */
    void addScore(int points){
        score += points;
        System.out.println("Gained " + points + " points. Total score: " + score);
    }

    /**
     * Removes one life from the player
     */
    void loseLife(){
        if (lives > 0){
            lives--;
            System.out.println("Lost a life! Lives left: " + lives);
        }
        if (lives == 0){
            System.out.println("Player is dead!");
            System.out.println("GAME OVER");
        }
    }
    /**
     * Returns the current score
     * 
     * @return current score
     */
    int getScore(){
        return score;
    }

    /**
     * Returns remain lives
     * 
     * @return remaining lives
     */
    int getLives(){
        return lives;
    }
}
public class OBJ51 {
    public static void main(String[] args){
        // create player stats within the package
        stats player = new stats(3);
        player.addScore(5);
        player.loseLife();
        player.addScore(10);
        player.loseLife();
        player.loseLife();
    } 
}
