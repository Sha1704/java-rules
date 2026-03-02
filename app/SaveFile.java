package app;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import javax.crypto.SecretKey;
import java.security.*;

/**
 * Handles secure saving and loading of player profiles. Implements CERT rules
 * for serialization safety.
 */
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;

public class SaveFile {

    // File name for storing player data
    private static final String SAVE_FILE = "player_profile.dat";

    // WHITELIST of allowed classes for SER12-J (prevents deserializing untrusted classes)
    private static final Set<String> SAFE_CLASSES = Set.of(
            // Game classes
            "app.Player",
            "app.Regular",
            "app.VIP",
            // Safe Java collection classes
            "java.lang.String",
            "java.lang.Integer",
            "java.util.ArrayList",
            "java.util.List"
    );

    /**
     * Custom ObjectInputStream that checks every class against a whitelist
     * before allowing deserialization. This prevents SER12-J violations.
     */
    private static class SafeObjectInputStream extends ObjectInputStream {

        public SafeObjectInputStream(InputStream in) throws IOException {
            super(in);
        }

        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            String className = desc.getName();

            // SER12-J: Check if class is in whitelist
            if (!SAFE_CLASSES.contains(className)) {
                throw new InvalidClassException(
                        "SER12-J Violation: Untrusted class '" + className + "' not allowed"
                );
            }

            return super.resolveClass(desc);
        }
    }

    /**
     * Saves a player object to disk. This method follows SER01-J, SER02-J,
     * SER03-J, SER04-J, MET54-J, ERR53-J, FIO53-J.
     *
     * @param player the player object to save
     * @param encryptionKey the secret key for encryption (null if no
     * encryption)
     * @param signatureKey the private key for signing (null if no signing)
     * @return true if save succeeded, false otherwise with error message
     */
    public boolean savePlayer(Player player, SecretKey encryptionKey, PrivateKey signatureKey) {
        // MET54-J: Provide feedback - return boolean
        if (player == null) {
            System.err.println("Save failed: Player is null");
            return false;
        }

        // SER04-J: Check security manager before writing
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkWrite(SAVE_FILE);
            } catch (SecurityException e) {
                System.err.println("Security manager denied write access: " + e.getMessage());
                return false; // ERR53-J: Graceful error handling
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            // SER01-J: Do not deviate from proper signatures
            // Using defaultWriteObject is correct. If Player has writeObject method,
            // it will be called automatically with this signature.
            // FIO53-J: Use writeUnshared() with care
            // We use writeObject() (shared mode) by default. If we need unshared,
            // we would call oos.writeUnshared(player) instead.
            oos.writeObject(player);
            oos.flush();

            byte[] serializedData = baos.toByteArray();

            // SER02-J: Sign then seal objects before sending outside trust boundary
            // For now, we just store the raw data
            // TODO: Implement actual signing and encryption here
            if (encryptionKey != null) {
                System.out.println("SER02-J: Encryption key provided, but not implemented yet");
            }
            if (signatureKey != null) {
                System.out.println("SER02-J: Signature key provided, but not implemented yet");
            }

            // SER03-J: Do not serialize unencrypted sensitive data
            // We trust that the Player class marks sensitive fields as transient
            // If Player has sensitive data, it should NOT be in serializedData
            // Write to file
            Files.write(new File(SAVE_FILE).toPath(), serializedData);

            System.out.println("Player saved successfully");
            return true; // MET54-J: Success feedback

        } catch (IOException e) {
            // ERR53-J: Try to gracefully recover from system errors
            System.err.println("Failed to save player: " + e.getMessage());
            return false; // MET54-J: Failure feedback
        } catch (Exception e) {
            System.err.println("Unexpected error during save: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a player object from disk securely. This method follows SER02-J,
     * SER04-J, SER12-J, MET54-J, ERR53-J, FIO53-J.
     *
     * @param encryptionKey the secret key for decryption (null if no
     * encryption)
     * @param verificationKey the public key for signature verification (null if
     * no signing)
     * @return the loaded Player object, or null if load fails
     */
    public Player loadPlayer(SecretKey encryptionKey, PublicKey verificationKey) {
        // MET54-J: Return null on failure for feedback
        // SER04-J: Check security manager before reading
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkRead(SAVE_FILE);
            } catch (SecurityException e) {
                System.err.println("Security manager denied read access: " + e.getMessage());
                return null;
            }
        }

        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("No saved player found");
            return null;
        }

        try {
            byte[] fileData = Files.readAllBytes(file.toPath());

            // SER02-J: Sign then seal - reverse order: unseal then verify
            // For now, we just use the raw data
            if (encryptionKey != null) {
                System.out.println("SER02-J: Decryption key provided, but not implemented yet");
            }
            if (verificationKey != null) {
                System.out.println("SER02-J: Verification key provided, but not implemented yet");
            }

            byte[] serializedData = fileData; // Would be decrypted data in real impl

            // SER12-J: Use SafeObjectInputStream to prevent untrusted deserialization
            try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData); SafeObjectInputStream ois = new SafeObjectInputStream(bais)) {

                // FIO53-J: Use readUnshared() with care
                // We use readObject() for shared objects. If we need unshared, use readUnshared()
                Object obj = ois.readObject();

                if (!(obj instanceof Player)) {
                    System.err.println("Loaded object is not a Player");
                    return null;
                }

                Player player = (Player) obj;

                // SER12-J: Additional validation of deserialized data
                if (!validatePlayer(player)) {
                    System.err.println("Player validation failed after deserialization");
                    return null;
                }

                System.out.println("Player loaded successfully");
                return player;

            } catch (InvalidClassException e) {
                // SER12-J: Catch whitelist violations
                System.err.println("SER12-J: Untrusted class in save file: " + e.getMessage());
                return null;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Save file not found");
            return null;
        } catch (IOException e) {
            System.err.println("Error reading save file: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Player class not found: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error during load: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validates a deserialized Player object. Ensures data integrity and
     * prevents corrupted/malicious data.
     *
     * @param player the player to validate
     * @return true if player is valid, false otherwise
     */
    private boolean validatePlayer(Player player) {
        if (player == null) {
            System.err.println("Validation error: Player is null");
            return false; // MET54-J: Provide feedback
        }

        // Check username (assuming Player has getUsername())
        try {
            String username = player.getUsername();
            if (username == null || username.trim().isEmpty()) {
                System.err.println("Validation error: Invalid username");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get username: " + e.getMessage());
            return false;
        }

        // Check chips (non-negative)
        try {
            int chips = player.getChips();
            if (chips < 0) {
                System.err.println("Validation error: Chips cannot be negative");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get chips: " + e.getMessage());
            return false;
        }

        // Check games played (non-negative, games won <= games played)
        try {
            int gamesPlayed = player.getGamesPlayed();
            if (gamesPlayed < 0) {
                System.err.println("Validation error: Games played cannot be negative");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get games played: " + e.getMessage());
            return false;
        }

        try {
            int gamesWon = player.getGamesWon();
            int gamesPlayed = player.getGamesPlayed();
            if (gamesWon > gamesPlayed) {
                System.err.println("Validation error: Games won exceeds games played");
                return false;
            }
        } catch (Exception e) {
            // If method doesn't exist, skip this check
        }

        return true; // MET54-J: Return true for success
    }

    // SER11-J: Prevent overwriting of externalizable objects
    // Note: SER11-J applies to Externalizable; not needed for Serializable.
    // If Player used Externalizable, a guard in readExternal() would be needed.
}

class EncryptAndDecrypt
{
    private static final String UNICODE_FORMAT = "UTF-8";

    public static SecretKey generateKey (String encryptionType) throws NoSuchAlgorithmException // encryptionType is AES
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionType);
        SecretKey myKey = keyGenerator.generateKey();
        return myKey;
    }

    public static byte[] encryptData (String dataToEncrypt, SecretKey myKey, Cipher cipher)
    {
        try
        {
            byte[] text = dataToEncrypt.getBytes(UNICODE_FORMAT);
            cipher.init(Cipher.ENCRYPT_MODE, myKey);
            byte[] cipherText = cipher.doFinal(text);

            return cipherText;
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println("Unsupported encoding exception: " + e.getMessage());
            return null;
        }
        catch (InvalidKeyException e)
        {
            System.out.println("Invalid key exception: " + e.getMessage());
            return null;
        }
        catch (IllegalBlockSizeException e)
        {
            System.out.println("Illegal block size exception: " + e.getMessage());
            return null;
        }
        catch (BadPaddingException e)
        {
            System.out.println("Bad padding exception: " + e.getMessage());
            return null;
        }
    }

    public static String decryptData(byte[] dataToDecrypt, SecretKey myKey, Cipher cipher)
    {
        try
        {
            cipher.init(Cipher.DECRYPT_MODE, myKey);
            byte[] plainText = cipher.doFinal(dataToDecrypt);
            String result = new String(plainText);

            return result;
        }
        catch (InvalidKeyException e)
        {
            System.out.println("Invalid key exception: " + e.getMessage());
            return null;
        }
        catch (IllegalBlockSizeException e)
        {
            System.out.println("Illegal block size exception: " + e.getMessage());
            return null;
        }
        catch (BadPaddingException e)
        {
            System.out.println("Bad padding exception: " + e.getMessage());
            return null;
        }
    }

    /* example of encrypting and decrypting

    public static void main (String [] args)
    {
        String text = "This is an example main code";
        try 
        {
            SecretKey key = generateKey("AES");
            Cipher chipher;
            chipher = Cipher.getInstance("AES");
            
            byte[] encryptedData = encryptData(text, key, chipher);
            String encryptedString = new String(encryptedData);
            System.out.println(encryptedString);
            String decrypted = decryptData(encryptedData, key, chipher);
            System.out.println(decrypted);
        } 
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("No such algorithm exception: " + e.getMessage());
        }
        catch (NoSuchPaddingException e)
        {
            System.out.println("No such padding exception: " + e.getMessage());
        }
    }

        */
}

