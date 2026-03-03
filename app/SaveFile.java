package app;

import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.file.Files;

/**
 * Handles secure saving and loading of player profiles. Implements CERT rules
 * for serialization safety.
 */
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

            // SER01-J: Do not deviate - using proper signature
            oos.writeObject(player); // Calls Player.writeObject if defined
            oos.flush();

            byte[] serializedData = baos.toByteArray();

            // SER02-J: Sign then seal
            if (encryptionKey == null || signatureKey == null) {
                System.err.println("SER02-J: Encryption and signature keys required");
                return false;
            }

            // Step 1: Sign the serialized data
            byte[] signature = signData(serializedData, signatureKey);
            if (signature == null) {
                System.err.println("SER02-J: Signing failed");
                return false;
            }

            // Combine data + signature
            ByteArrayOutputStream combined = new ByteArrayOutputStream();
            combined.write(serializedData);
            combined.write(signature);
            byte[] dataToSeal = combined.toByteArray();

            // Step 2: Encrypt (seal) the signed data
            byte[] sealedData = encryptData(dataToSeal, encryptionKey);
            if (sealedData == null) {
                System.err.println("SER02-J: Encryption failed");
                return false;
            }

            // Write sealed data to file
            Files.write(new File(SAVE_FILE).toPath(), sealedData);

            System.out.println("Player saved securely (SER02-J: sign-then-seal)");
            return true;

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
        // SER04-J: Security manager check before reading
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkRead(SAVE_FILE);
            } catch (SecurityException e) {
                System.err.println("SER04-J: Security manager blocked read: " + e.getMessage());
                return null;
            }
        }

        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            System.out.println("No saved player found");
            return null;
        }

        try {
            byte[] sealedData = Files.readAllBytes(file.toPath());
            if (encryptionKey == null || verificationKey == null) {
                System.err.println("SER02-J: Decryption and verification keys required");
                return null;
            }
            
            // SER02-J: Unseal first (decrypt)
            byte[] unsealedData = decryptData(sealedData, encryptionKey);
            if (unsealedData == null) {
                System.err.println("SER02-J: Decryption failed");
                return null;
            }
            
            // Split data and signature (signature is 256 bytes for RSA-2048)
            int sigLen = 256;
            if (unsealedData.length <= sigLen) {
                System.err.println("SER02-J: Data too short to contain signature");
                return null;
            }
            byte[] serializedData = new byte[unsealedData.length - sigLen];
            byte[] signature = new byte[sigLen];
            System.arraycopy(unsealedData, 0, serializedData, 0, serializedData.length);
            System.arraycopy(unsealedData, serializedData.length, signature, 0, sigLen);
            
            // SER02-J: Then verify signature
            if (!verifySignature(serializedData, signature, verificationKey)) {
                System.err.println("SER02-J: Signature verification failed - data may be tampered");
                return null;
            }
            
            // SER12-J: Safe deserialization
            try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
                 SafeObjectInputStream ois = new SafeObjectInputStream(bais)) {
                
                Object obj = ois.readObject();
                if (!(obj instanceof Player)) {
                    System.err.println("Load error: Not a Player object");
                    return null;
                }
                
                Player player = (Player) obj;
                
                // SER12-J: Additional validation
                if (!validatePlayer(player)) {
                    System.err.println("Player validation failed after deserialization");
                    return null;
                }
                
                System.out.println("Player loaded securely (SER02-J: verified+decrypted)");
                return player;
                
            } catch (InvalidClassException e) {
                System.err.println("SER12-J: Untrusted class: " + e.getMessage());
                return null;
            }
            
        } catch (IOException e) {
            System.err.println("Error reading save file (ERR53-J): " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e) {
            System.err.println("Player class not found (ERR53-J): " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected load error (ERR53-J): " + e.getMessage());
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

        // Check playerId (non-null, non-empty)
        try {
            String pid = player.getPlayerId();
            if (pid == null || pid.trim().isEmpty()) {
                System.err.println("Validation error: Invalid playerId");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get playerId: " + e.getMessage());
            return false;
        }

        // Check name (non-null, non-empty)
        try {
            String name = player.getName();
            if (name == null || name.trim().isEmpty()) {
                System.err.println("Validation error: Invalid name");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get name: " + e.getMessage());
            return false;
        }

        // Check chipBalance (non-negative)
        try {
            int chips = player.getChipBalance();
            if (chips < 0) {
                System.err.println("Validation error: Negative chipBalance");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get chipBalance: " + e.getMessage());
            return false;
        }

        // SER03-J: Verify transient field (email) is null after deserialization
        try {
            String email = player.getEmail(); // assumes Player has getEmail()
            if (email != null) {
                System.err.println("SER03-J Violation: email should be null (transient)");
                return false;
            }
        } catch (Exception e) {
            // If method doesn't exist, skip
        }

        return true; // MET54-J
    }

    // SER11-J: Prevent overwriting of externalizable objects
    // Note: SER11-J applies to Externalizable; not needed for Serializable.
    // If Player used Externalizable, a guard in readExternal() would be needed.

    // Helper function
    // SER02-J: Sign data with private key
    private byte[] signData(byte[] data, PrivateKey privateKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(privateKey);
            sig.update(data);
            return sig.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.err.println("Signing error: " + e.getMessage());
            return null;
        }
    }

    // SER02-J: Encrypt data with AES
    private byte[] encryptData(byte[] data, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Encryption error: " + e.getMessage());
            return null;
        }
    }
}

class EncryptAndDecrypt {

    private static final String UNICODE_FORMAT = "UTF-8";

    public static SecretKey generateKey(String encryptionType) throws NoSuchAlgorithmException // encryptionType is AES
    {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionType);
        SecretKey myKey = keyGenerator.generateKey();
        return myKey;
    }

    public static byte[] encryptData(String dataToEncrypt, SecretKey myKey, Cipher cipher) {
        try {
            byte[] text = dataToEncrypt.getBytes(UNICODE_FORMAT);
            cipher.init(Cipher.ENCRYPT_MODE, myKey);
            byte[] cipherText = cipher.doFinal(text);

            return cipherText;
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding exception: " + e.getMessage());
            return null;
        } catch (InvalidKeyException e) {
            System.out.println("Invalid key exception: " + e.getMessage());
            return null;
        } catch (IllegalBlockSizeException e) {
            System.out.println("Illegal block size exception: " + e.getMessage());
            return null;
        } catch (BadPaddingException e) {
            System.out.println("Bad padding exception: " + e.getMessage());
            return null;
        }
    }

    public static String decryptData(byte[] dataToDecrypt, SecretKey myKey, Cipher cipher) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, myKey);
            byte[] plainText = cipher.doFinal(dataToDecrypt);
            String result = new String(plainText);

            return result;
        } catch (InvalidKeyException e) {
            System.out.println("Invalid key exception: " + e.getMessage());
            return null;
        } catch (IllegalBlockSizeException e) {
            System.out.println("Illegal block size exception: " + e.getMessage());
            return null;
        } catch (BadPaddingException e) {
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
