
import java.io.*;
import java.util.*;

/**
 * Rule SER11-J: Prevent overwriting of externalizable objects using a Balatro
 * save
 *
 * cvarga9
 */
public class SER11 {

    // This is for testing/viewing, TRUE deletes it automatically while FALSE keeps the file on your computer
    private static final boolean REMOVE_SAVE_FILE = true;

    // WHITELIST of allowed classes
    private static final Set<String> SAFE_CLASSES = Set.of(
            // Game class
            "SER11$BalatroExternalizableSave",
            // Safe Java classes that the save file uses
            "java.lang.String",
            "java.lang.Integer",
            "java.util.ArrayList",
            "java.util.LinkedList"
    );

    /**
     * Custom ObjectInputStream that validates classes against a whitelist
     * before allowing them to be deserialized
     */
    private static class SafeObjectInputStream extends ObjectInputStream {

        public SafeObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            String className = desc.getName();

            // Checking if class is in whitelist
            if (!SAFE_CLASSES.contains(className)) {
                throw new InvalidClassException(
                        "SER12-J Violation: Untrusted class '" + className + "' not in whitelist"
                );
            }

            // Class is safe, proceed
            return super.resolveClass(desc);
        }
    }

    /**
     * A Balatro game save that implements Externalizable with a guard against
     * multiple initialization (SER11-J)
     */
    public static class BalatroExternalizableSave implements Externalizable {

        private String username;
        private int totalChips;
        private int currentAnte;
        private List<String> activeJokers;
        private List<String> unlockedTarots;
        @SuppressWarnings("unused")
        private transient String sessionSeed;

        // SER11-J
        private final Object lock = new Object();
        private boolean initialized = false;

        /**
         * Public no-argument constructor required by Externalizable
         */
        public BalatroExternalizableSave() {
        }

        /**
         * Constructs a new game save with initial values.
         *
         * @param username player's public username
         */
        public BalatroExternalizableSave(String username) {
            this.username = username;
            this.totalChips = 0;
            this.currentAnte = 1;
            this.activeJokers = new ArrayList<>();
            this.unlockedTarots = new ArrayList<>();
            generateSeed();
        }

        //SER11-J
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // Write all non-transient fields in order
            out.writeObject(username);
            out.writeInt(totalChips);
            out.writeInt(currentAnte);
            out.writeObject(activeJokers);
            out.writeObject(unlockedTarots);
        }

        //SER11-J
        @SuppressWarnings("unchecked") //Just done to get rid of annoying warning
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            synchronized (lock) {
                if (!initialized) {
                    // Read fields in same order
                    username = (String) in.readObject();
                    totalChips = in.readInt();
                    currentAnte = in.readInt();
                    activeJokers = (List<String>) in.readObject();
                    unlockedTarots = (List<String>) in.readObject();

                    // Re‑generate seed
                    generateSeed();

                    initialized = true;
                } else {
                    throw new IllegalStateException(
                            "SER11-J: readExternal() called already"
                    );
                }
            }
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
         * Adds a joker card to active jokers.
         *
         * @param jokerName The name of the joker card to add
         */
        public void addJoker(String jokerName) {
            if (activeJokers.size() < 5) {
                activeJokers.add(jokerName);
            }
        }

        /**
         * Unlocks a tarot card.
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
         * Returns a string representation of the game state.
         *
         * @return Game state summary
         */
        @Override
        public String toString() {
            return "Player: " + username + " | Chips: " + totalChips
                    + " | Ante: " + currentAnte + " | Jokers: " + activeJokers.size()
                    + " | Tarots: " + unlockedTarots.size();
        }
    }

    /**
     * Safely loads a game save using whitelisting
     *
     * @param file The save file being loaded
     * @return deserialized save if valid
     * @throws IOException if an error occurs
     * @throws ClassNotFoundException if class cannot be found
     */
    private static BalatroExternalizableSave loadSafe(File file) throws IOException, ClassNotFoundException {
        try (SafeObjectInputStream in = new SafeObjectInputStream(new FileInputStream(file))) {
            return (BalatroExternalizableSave) in.readObject();
        }
    }

    /**
     * Code to show serialization and SER11-J protection
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        // Create a game save
        BalatroExternalizableSave save = new BalatroExternalizableSave("Carlos");

        // Game progress
        save.addJoker("Jolly Joker");
        save.addJoker("Zany Joker");
        save.unlockTarot("The Fool");
        save.updateChips(2500);

        System.out.println("SER11-J\n");
        System.out.println("Original object:");
        System.out.println("  " + save);

        try {
            // Save the game
            File saveFile = new File("balatro_ext_save.dat");
            try (FileOutputStream fileOut = new FileOutputStream(saveFile); ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(save);
            }

            System.out.println("\nGame saved to: " + saveFile.getAbsolutePath());

            // Load the game
            System.out.println("\nLoading (first readExternal):");
            BalatroExternalizableSave loadedSave = loadSafe(saveFile);
            System.out.println(" Game loaded successfully!");
            System.out.println(" Loaded state: " + loadedSave);

            // Calling readExternal again (will break)
            System.out.println("\nreadExternal on the same object, should not work");
            try {
                // Create a new input stream from the same file
                try (FileInputStream fileIn = new FileInputStream(saveFile); ObjectInputStream in = new ObjectInputStream(fileIn)) {
                    loadedSave.readExternal(in); // Should throw IllegalStateException
                }
            } catch (IllegalStateException e) {
                System.out.println(" SER11-J followed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println(" SER11-J not followed: " + e);
            }

            // Cleanup
            if (REMOVE_SAVE_FILE) {
                if (saveFile.delete()) {
                    System.out.println("\nCleanup: Save file deleted.");
                } else {
                    System.out.println("\nCleanup: Could not delete save file.");
                }
            } else {
                System.out.println("\nCleanup: Save file preserved at: " + saveFile.getAbsolutePath());
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error during serialization: " + e.getMessage());
        }
    }
}
