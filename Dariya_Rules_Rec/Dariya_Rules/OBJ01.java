// OBJ01-J. Limit accessibility of fields
// Implement player score tracking with controlled access
package Dariya_Rules;

public class OBJ01 {
    // private field to store player score
    private int score = 0;

    // method to safely read the score
    public int getScore() {
        return score;
    }

    // method to add points to the score safely
    // ensure class invariants are maintained
    public void addPoints(int points) {
        score += points;
    }
    public static void main(String[] args) {
        OBJ01 stats = new OBJ01();
        // add points to player score
        stats.addPoints(10);
        stats.addPoints(10);
        System.out.println("Player score: " + stats.getScore());
    }
}

