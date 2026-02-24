
import java.io.*;
import java.util.*;

/**
 * Rule SER04-J: Do not allow serialization and deserialization to bypass the
 * security manager using a Balatro game save
 *
 * cvarga9
 */
public class SER04 {

    // This is for testing/viewing, TRUE deletes it automatically while FALSE keeps the file on your computer
    private static final boolean REMOVE_SAVE_FILE = true;

    public static class BalatroSecureSave implements Serializable {

        // Serialization version ID
        private static final long serialVersionUID = 1L;

        // Game variables
        private final String playerName;
        private int totalChips;
        @SuppressWarnings("FieldMayBeFinal") //The warning was annoying, this would change in the real world
        private int currentAnte;
        private List<String> activeJokers;
        @SuppressWarnings("FieldMayBeFinal") //The warning was annoying, this would change in the real world
        private List<String> unlockedTarots;
        @SuppressWarnings("unused") //The warning was annoying, this would be used in the real world for generation
        private transient String sessionSeed;

        // Simple anti-cheat: Just check limits
        private void validateNoCheating() {
            if (totalChips > 10000000) {
                throw new SecurityException("Anti-cheat: Impossible chip count");
            }
            if (activeJokers.size() > 5) {
                throw new SecurityException("Anti-cheat: Too many jokers");
            }
        }

        /**
         * Creates a new Balatro game save
         *
         * @param playerName - Players name
         */
        public BalatroSecureSave(String playerName) {
            this.playerName = playerName;
            this.totalChips = 0;
            this.currentAnte = 1;
            this.activeJokers = new ArrayList<>();
            this.unlockedTarots = new ArrayList<>();
            validateNoCheating();
            generateSeed();
        }

        /**
         * Serialization - Validates game state and prepares data for storage.
         *
         * @param out The ObjectOutputStream to write to
         * @throws IOException if an error occurs
         */
        private void writeObject(ObjectOutputStream out) throws IOException {
            validateNoCheating(); // SER04-J
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
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            validateNoCheating(); // SER04-J
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
        private void generateSeed() {
            this.sessionSeed = "SEED_" + System.currentTimeMillis() + "_"
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
        BalatroSecureSave save = new BalatroSecureSave("Carlos");

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

            BalatroSecureSave loadedSave;
            try (FileInputStream fileIn = new FileInputStream(saveFile); ObjectInputStream in = new ObjectInputStream(fileIn)) {
                loadedSave = (BalatroSecureSave) in.readObject();
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
