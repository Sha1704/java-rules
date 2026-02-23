// OBJ51-J. Minimize the accessibility of classes and their members
// implement player stats with minimal accessibility
package Dariya_Rec;

final class stats{
    private int score;
    private int lives;

    // package-private constructor
    stats(int initialLives){
        this.score = 0;
        this.lives = initialLives;
    }
    // package-private method to add points
    void addScore(int points){
        score += points;
        System.out.println("Gained " + points + " points. Total score: " + score);
    }
    // package-private method to lose a life
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
    // package-private getters
    int getScore(){
        return score;
    }
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
