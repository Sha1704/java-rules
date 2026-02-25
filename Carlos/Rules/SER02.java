
import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;

/**
 * Rule SER02-J: Sign then seal objects before sending them outside a trust
 * boundary using a balatro save
 *
 * cvarga9
 */
public class SER02 {

    // This is for testing/viewing, TRUE deletes it automatically while FALSE keeps the file on your computer
    private static final boolean REMOVE_SAVE_FILE = true;

    // WHITELIST of allowed classes for SER12-J (includes sealed/signed wrappers)
    private static final Set<String> SAFE_CLASSES = Set.of(
            // Game class
            "SER02$BalatroSecureSave",
            // Security wrapper classes
            "javax.crypto.SealedObject",
            "java.security.SignedObject",
            // Safe Java classes that the save file uses
            "java.lang.String",
            "java.lang.Integer",
            "java.util.ArrayList",
            "java.util.LinkedList",
            "java.util.HashMap",
            "[B" // byte array (used by crypto)
    );

    /**
     * Custom ObjectInputStream that validates classes against a whitelist
     * before allowing them to be deserialized (SER12-J)
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
     * A Balatro game save that contains sensitive data (email, password hash)
     * and is intended to be signed and sealed before transmission
     */
    public static class BalatroSecureSave implements Serializable {

        // Serialization version ID
        private static final long serialVersionUID = 1L;

        // Game vars (Ignore supress warnings, it was annoying)
        private final String username;
        @SuppressWarnings("FieldMayBeFinal")
        private String email;
        @SuppressWarnings("FieldMayBeFinal")
        private String passwordHash;
        private int totalChips;
        @SuppressWarnings("FieldMayBeFinal")
        private int currentAnte;
        @SuppressWarnings("FieldMayBeFinal")
        private List<String> activeJokers;
        @SuppressWarnings("FieldMayBeFinal")
        private List<String> unlockedTarots;
        @SuppressWarnings("unused")
        private transient String sessionSeed;

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
         * @param username player's public username
         * @param email player's email address (sensitive)
         * @param password player's password (will be hashed for demo)
         */
        public BalatroSecureSave(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.passwordHash = "Hash: " + password; //Ignore password being stored in plaintext, this isnt a real application I wouldnt do that
            this.totalChips = 0;
            this.currentAnte = 1;
            this.activeJokers = new ArrayList<>();
            this.unlockedTarots = new ArrayList<>();
            validateNoCheating();
            generateSeed();
        }

        /**
         * Generates a new random seed for the game session
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
            if (activeJokers.size() < 5) {
                activeJokers.add(jokerName);
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
         * Updates chip total
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
         * @return email
         */
        public String getEmail() {
            return email;
        }

        /**
         * @return password hash
         */
        public String getPasswordHash() {
            return passwordHash;
        }

        /**
         * Returns a string representation of the game state (non‑sensitive
         * only)
         */
        @Override
        public String toString() {
            return "Player: " + username + " | Chips: " + totalChips
                    + " | Ante: " + currentAnte + " | Jokers: " + activeJokers.size()
                    + " | Tarots: " + unlockedTarots.size();
        }
    }

    /**
     * Safely loads a game save using SER12 whitelisting.
     *
     * @param file The save file being loaded
     * @return deserialized object (should be a SealedObject)
     * @throws IOException if an error occurs
     * @throws ClassNotFoundException if class cannot be found
     */
    private static Object loadSafe(File file) throws IOException, ClassNotFoundException {
        try (SafeObjectInputStream in = new SafeObjectInputStream(new FileInputStream(file))) {
            return in.readObject();
        }
    }

    /**
     * Code to show SER02
     */
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        // Create a game save
        BalatroSecureSave save = new BalatroSecureSave("Carlos", "cvarga9@ilstu.edu", "Password123!"
        );

        // Game progress
        save.addJoker("Jolly Joker");
        save.addJoker("Zany Joker");
        save.unlockTarot("The Fool");
        save.updateChips(2500);

        System.out.println("Current game state: " + save);
        System.out.println("Sensitive Data (SER03): Email: " + save.email + " Password Hash: " + save.passwordHash);

        try {
            // Save the game (SER02)
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair signKeyPair = kpg.generateKeyPair();
            Signature signingEngine = Signature.getInstance("SHA256withRSA");

            // Sealing key (symmetric)
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            SecretKey sealKey = keyGen.generateKey();
            Cipher cipher = Cipher.getInstance("AES");

            // Signing (SER02)
            SignedObject signedSave = new SignedObject(save, signKeyPair.getPrivate(), signingEngine);

            // Then seal the signed object
            cipher.init(Cipher.ENCRYPT_MODE, sealKey);
            SealedObject sealedSave = new SealedObject(signedSave, cipher);

            // Saving to file
            File saveFile = new File("balatro_signed_sealed.dat");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
                oos.writeObject(sealedSave);
            }
            System.out.println("\nGame saved to: " + saveFile.getAbsolutePath());

            SealedObject receivedSealed = (SealedObject) loadSafe(saveFile);

            // Unseal secret key
            cipher.init(Cipher.DECRYPT_MODE, sealKey);
            SignedObject receivedSigned = (SignedObject) receivedSealed.getObject(cipher);

            // Verify the signature
            if (!receivedSigned.verify(signKeyPair.getPublic(), signingEngine)) {
                throw new SecurityException("Signature verification failed");
            }

            BalatroSecureSave receivedSave = (BalatroSecureSave) receivedSigned.getObject();

            System.out.println("\nGame loaded successfully");
            System.out.println("Loaded state: " + receivedSave);
            System.out.println("Email after unseal: " + receivedSave.getEmail() + " PasswordHash after unseal: " + receivedSave.getPasswordHash());

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

        } catch (Exception e) {
            System.err.println("Error during signing/sealing: " + e.getMessage());
        }
    }
}
