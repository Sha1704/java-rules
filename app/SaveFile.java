package app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.Set;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class SaveFile {

    // File name for storing player data
    private static final String SAVE_FILE = "player_profile.dat";

    // WHITELIST of allowed classes for SER12-J (prevents deserializing untrusted
    // classes)
    private static final Set<String> SAFE_CLASSES = Set.of(
            // Game classes
            "app.Player",
            "app.Regular",
            "app.VIP",
            // Safe Java collection classes
            "java.lang.String",
            "java.lang.Integer",
            "java.util.ArrayList",
            "java.util.List");

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
                        "SER12-J Violation: Untrusted class '" + className + "' not allowed");
            }

            return super.resolveClass(desc);
        }
    }

    /**
     * Saves a player object to disk. This method follows SER01-J, SER02-J,
     * SER03-J, SER04-J, MET54-J, ERR53-J, FIO53-J.
     *
     * @param player        the player object to save
     * @param encryptionKey the secret key for encryption
     * @param signatureKey  the private key for signing
     * @return true if save succeeded, false otherwise with error message
     */
    public boolean savePlayer(Player player, SecretKey encryptionKey, PrivateKey signatureKey) {
        // MET54-J: Provide feedback - return boolean
        if (player == null) {
            System.err.println("Save failed: Player is null");
            return false;
        }

        // SER04-J: Check security manager before writing
        @SuppressWarnings("removal") // SER04 requires us to use SecurityManager, which is causing this
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            try {
                sm.checkWrite(SAVE_FILE);
            } catch (SecurityException e) {
                System.err.println("Security manager denied write access: " + e.getMessage());
                return false; // ERR53-J: Graceful error handling
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {

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
     * @param encryptionKey   the secret key for decryption
     * @param verificationKey the public key for signature verification
     * @return the loaded Player object, or null if load fails
     */
    public Player loadPlayer(SecretKey encryptionKey, PublicKey verificationKey) {
        // SER04-J: Security manager check before reading
        @SuppressWarnings("removal") // SER04 requires us to use SecurityManager, which is causing this
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

            // Split data and signature
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

        // // Check chipBalance (non-negative)
        // try {
        //     //int chips = player.getChipBalance();
        //     if (chips < 0) {
        //         System.err.println("Validation error: Negative chipBalance");
        //         return false;
        //     }
        // } catch (Exception e) {
        //     System.err.println("Validation error: Cannot get chipBalance: " + e.getMessage());
        //     return false;
        // }

        // SER03-J: Verify transient field (playerId) is null after deserialization
        try {
            // int pID = player.getPlayerId();
            if (player.getPlayerId() != 0) {
                System.err.println("SER03-J Violation: playerId should be 0 (transient)");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Validation error: Cannot get playerId: " + e.getMessage());
            return false;
        }

        return true; // MET54-J
    }

    /**
     * SER02-J: Signs the given data using the specified private key.
     *
     * @param data       the data to sign
     * @param privateKey the private key for signing
     * @return the signature as a byte array, or null if signing fails
     */
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

    /**
     * SER02-J: Encrypts the given data using the specified secret key.
     *
     * @param data the data to encrypt
     * @param key  the secret key for encryption
     * @return the encrypted data as a byte array, or null if encryption fails
     */
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

    /**
     * SER02-J: Verifies the signature of the given data using the specified
     * public key.
     *
     * @param data      the data that was signed
     * @param signature the signature to verify
     * @param publicKey the public key for verification
     * @return true if the signature is valid, false otherwise
     */
    private boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            System.err.println("Signature verification error: " + e.getMessage());
            return false;
        }
    }

    /**
     * SER02-J: Decrypts (unseals) the given encrypted data using the specified
     * secret key.
     *
     * @param encryptedData the data to decrypt
     * @param key           the secret key for decryption
     * @return the decrypted data as a byte array, or null if decryption fails
     */
    private byte[] decryptData(byte[] encryptedData, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(encryptedData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Decryption error: " + e.getMessage());
            return null;
        }
    }
}

class EncryptAndDecrypt {

    private static final String UNICODE_FORMAT = "UTF-8";

    public static SecretKey generateKey(String encryptionType) throws Exception // encryptionType is AES
    {
        // Generate AES key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(encryptionType);
        SecretKey myKey = keyGenerator.generateKey();

        // Convert keu to base 64 string for storage
        String encodedKey = Base64.getEncoder().encodeToString(myKey.getEncoded());

        // Database connection info
        String DB_URL = "jdbc:oracle:thin:@10.110.10.90:1521:oracle";
        String USER = "IT326S09";
        String PASS = "pink22";

        String query = "INSERT INTO account_balance ('key') VALUES (?)"; 
        
        Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, encodedKey);
        stmt.executeUpdate();

        stmt.close();
        conn.close();

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

    /*
     * example of encrypting and decrypting
     * 
     * public static void main (String [] args)
     * {
     * String text = "This is an example main code";
     * try
     * {
     * SecretKey key = generateKey("AES");
     * Cipher chipher;
     * chipher = Cipher.getInstance("AES");
     * 
     * byte[] encryptedData = encryptData(text, key, chipher);
     * String encryptedString = new String(encryptedData);
     * System.out.println(encryptedString);
     * String decrypted = decryptData(encryptedData, key, chipher);
     * System.out.println(decrypted);
     * }
     * catch (NoSuchAlgorithmException e)
     * {
     * System.out.println("No such algorithm exception: " + e.getMessage());
     * }
     * catch (NoSuchPaddingException e)
     * {
     * System.out.println("No such padding exception: " + e.getMessage());
     * }
     * }
     * 
     */
}
