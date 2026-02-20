
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
        private void writeObject(ObjectOutputStream out) throws IOException { //Follows Rec FIO53-J
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
        private void readObject(ObjectInputStream in) //Follows Rec FIO53-J
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
         * @return true if the joker was added (max 5 not reached), false
         * otherwise
         */
        public boolean addJoker(String jokerName) {
            if (activeJokers.size() < 5) { // Max 5 jokers
                activeJokers.add(jokerName);
                System.out.println("Added joker: " + jokerName);
                return true;
            }
            System.out.println("Cannot add joker " + jokerName + " (limit reached)");
            return false;
        }

        /**
         * Unlocks a tarot card
         *
         * @param tarotName The name of the tarot card to unlock
         * @return true if the tarot was newly unlocked, false if already
         * unlocked
         */
        public boolean unlockTarot(String tarotName) {
            if (!unlockedTarots.contains(tarotName)) {
                unlockedTarots.add(tarotName);
                System.out.println("Unlocked tarot: " + tarotName);
                return true;
            }
            System.out.println("Tarot " + tarotName + " already unlocked");
            return false;
        }

        /**
         * Updates chip total
         *
         * @param chips The number of chips to add
         * @return The new total chips (capped 0 if negative)
         */
        public int updateChips(int chips) {
            this.totalChips += chips;
            if (this.totalChips < 0) {
                this.totalChips = 0;
            }
            return this.totalChips;
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
    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    public static void main(String[] args) {
        // Create a game save
        BalatroSave save = new BalatroSave("Carlos");

        // Game progress
        System.out.println("Adding jokers:");
        boolean added1 = save.addJoker("Jolly Joker");
        boolean added2 = save.addJoker("Zany Joker");
        boolean added3 = save.addJoker("Jolly Joker");
        boolean added4 = save.addJoker("Sly Joker");
        boolean added5 = save.addJoker("Crafty Joker");
        boolean added6 = save.addJoker("Abstract Joker"); // should fail (max 5)
        System.out.println("Add results: " + added1 + ", " + added2 + ", " + added3 + ", " + added4 + ", " + added5 + ", " + added6);

        System.out.println("\nUnlocking tarots:");
        boolean unlocked1 = save.unlockTarot("The Fool");
        boolean unlocked2 = save.unlockTarot("The Magician");
        boolean unlocked3 = save.unlockTarot("The Fool"); // already unlocked
        System.out.println("Unlock results: " + unlocked1 + ", " + unlocked2 + ", " + unlocked3);

        System.out.println("\nUpdating chips:");
        int newTotal = save.updateChips(2500);
        System.out.println("Chips after +2500: " + newTotal);
        newTotal = save.updateChips(-3000);
        System.out.println("Chips after -3000 (should cap at 0): " + newTotal);

        System.out.println("\nCurrent game state: " + save);
        File saveFile = new File("balatro_save.dat");

        try {
            // Save the game
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

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during serialization: " + e.getMessage());
        } catch (Throwable t) {   // ERR53‑J: catch system errors
            System.err.println("FATAL ERROR: " + t.getClass().getSimpleName() + " - " + t.getMessage());
            t.printStackTrace();
        } finally {
            // Cleanup – runs even after a Throwable
            if (REMOVE_SAVE_FILE) {
                if (saveFile.exists() && saveFile.delete()) {
                    System.out.println("Cleanup: Save file deleted.");
                } else {
                    System.out.println("Cleanup: Could not delete save file.");
                }
            } else {
                System.out.println("Cleanup: Save file preserved at: " + saveFile.getAbsolutePath());
            }
        }
    }
}
