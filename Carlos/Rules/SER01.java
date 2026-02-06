
import java.io.*;
import java.util.*;

/**
 * Rule SER01-J: Do not deviate from the proper signatures of serialization
 * methods using a Balatro game save
 *
 * cvarga9
 */
public class SER01 {

    // This is for testing/viewing, TRUE deletes it automatically while FALSE keeps the file on your computer
    private static final boolean REMOVE_SAVE_FILE = true;

    public static class BalatroSave implements Serializable {

        // Serialization version ID
        private static final long serialVersionUID = 1L;

        // Game variables
        private String playerName;
        private int totalChips;
        private int currentAnte;
        private List<String> activeJokers;
        private List<String> unlockedTarots;

        /**
         * Creates a new Balatro game save
         *
         * @param playerName - Players name
         */
        public BalatroSave(String playerName) {
            this.playerName = playerName;
            this.totalChips = 0;
            this.currentAnte = 1;
            this.activeJokers = new ArrayList<>();
            this.unlockedTarots = new ArrayList<>();
            generateSeed();
        }

        /**
         * Serialization - Validates game state and prepares data for storage.
         *
         * @param out The ObjectOutputStream to write to
         * @throws IOException if an error occurs
         */
        private void writeObject(ObjectOutputStream out) throws IOException {
            // Check player name
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player";
            }

            // Ensure positive values as negative is not possible (Reasonably not possible)
            if (totalChips < 0) {
                totalChips = 0;
            }
            if (currentAnte < 1) {
                currentAnte = 1;
            }

            // Logging
            System.out.println("Saving Balatro game for: " + playerName);

            // Default serialization
            out.defaultWriteObject();
        }

        /**
         * Deserialization - Validates loaded data and reinitializes transient
         * fields.
         *
         * @param in The ObjectInputStream to read from
         * @throws IOException if an error occurs
         * @throws ClassNotFoundException if class cannot be found
         */
        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            // Default deserialization
            in.defaultReadObject();

            // Reinitialize transient field with new seed
            generateSeed();

            // Validate loaded data
            if (totalChips > 10000000) {
                totalChips = 10000000; // Cap to prevent cheating
            }

            if (totalChips < 0) {
                totalChips = 0; // No negative chips
            }

            // Fix loaded data if corrupt
            if (activeJokers == null) {
                activeJokers = new ArrayList<>();
            }

            if (unlockedTarots == null) {
                unlockedTarots = new ArrayList<>();
            }

            System.out.println("Loaded Balatro game for: " + playerName);
        }

        /**
         * Replaces deserialized object with validated instance, ensures game
         * save integrity
         *
         * @return A validated BalatroSave instance
         */
        protected Object readResolve() {
            // Generate fresh session seed
            generateSeed();

            // Ensure jokers exist
            if (this.activeJokers == null) {
                this.activeJokers = new ArrayList<>();
            }

            return this;
        }

        /**
         * Generates a new random seed for the game session
         *
         * @return A random seed string
         */
        private String generateSeed() {
            return "SEED_" + System.currentTimeMillis() + "_"
                    + (int) (Math.random() * 1000);
        }

        /**
         * Adds a joker card to active jokers
         *
         * @param jokerName The name of the joker card to add
         */
        public void addJoker(String jokerName) {
            if (activeJokers.size() < 5) { // Max 5 jokers
                activeJokers.add(jokerName);
                System.out.println("Added joker: " + jokerName);
            }
        }

        /**
         * Unlocks a tarot card
         *
         * @param tarotName The name of the tarot card to unlock
         */
        public void unlockTarot(String tarotName) {
            if (!unlockedTarots.contains(tarotName)) {
                unlockedTarots.add(tarotName);
            }
        }

        /**
         * Updates chip total.
         *
         * @param chips The number of chips to add
         */
        public void updateChips(int chips) {
            this.totalChips += chips;
            if (this.totalChips < 0) {
                this.totalChips = 0;
            }
        }

        /**
         * Returns a string representation of the game state
         *
         * @return Game state summary
         */
        @Override
        public String toString() {
            return "Player: " + playerName + " | Chips: " + totalChips + " | Ante: " + currentAnte + " | Jokers: " + activeJokers.size() + " | Tarots: " + unlockedTarots.size();
        }
    }

    /**
     * Code to show serialization
     */
    public static void main(String[] args) {
        // Create a game save
        BalatroSave save = new BalatroSave("Carlos");

        // Game progress
        save.addJoker("Jolly Joker");
        save.addJoker("Zany Joker");
        save.unlockTarot("The Fool");
        save.updateChips(2500);

        System.out.println("Current game state: " + save);

        try {
            // Save the game
            File saveFile = new File("balatro_save.dat");
            try (FileOutputStream fileOut = new FileOutputStream(saveFile); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(save);
            }

            System.out.println("\nGame saved to: " + saveFile.getAbsolutePath());

            BalatroSave loadedSave;
            try (FileInputStream fileIn = new FileInputStream(saveFile); ObjectInputStream in = new ObjectInputStream(fileIn)) {
                loadedSave = (BalatroSave) in.readObject();
            }

            System.out.println("Game loaded successfully!");
            System.out.println("Loaded state: " + loadedSave);

            // Cleanup
            if (REMOVE_SAVE_FILE) {
                if (saveFile.delete()) {
                    System.out.println("Cleanup: Save file deleted.");
                } else {
                    System.out.println("Cleanup: Could not delete save file.");
                }
            } else {
                System.out.println("Cleanup: Save file preserved at: " + saveFile.getAbsolutePath());
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during serialization: " + e.getMessage());
        }
    }
}
