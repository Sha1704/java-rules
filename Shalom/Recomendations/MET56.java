package Shalom.Recomendations;

import java.security.MessageDigest;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class MET56 {

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
