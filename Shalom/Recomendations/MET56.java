package Shalom.Recomendations;

import java.security.MessageDigest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * RECOMENDATION MET56-J: Do not use .equals() to 
 * compare cryptographic keys because it may not 
 * compare the key values.
 */
public class MET56 {

    /**
     * Main method to generate and compare AES keys.
     * @param args command-line arguments
     * @throws Exception if cryptographic operations fail
     */
    public static void main(String[] args) throws Exception {

        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(256);

        SecretKey key1 = gen.generateKey();
        SecretKey key2 = gen.generateKey();

        boolean equal = MessageDigest.isEqual(
                key1.getEncoded(),
                key2.getEncoded()
        );

        System.out.println("Keys equal: " + equal);
    }
}
