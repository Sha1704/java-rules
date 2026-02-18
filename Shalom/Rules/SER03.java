package Shalom.Rules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class Encryption {

    private static final String SECRET_KEY = "1234567890123456";

    public static String encrypt(String strToEncrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(
                    strToEncrypt.getBytes());

            return Base64.getEncoder()
                    .encodeToString(encryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String strToDecrypt) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decodedBytes = Base64.getDecoder()
                    .decode(strToDecrypt);

            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

class Serialize {

    public void serializeData(String outFileName, Object obj) {
        try {
            FileOutputStream fileout = new FileOutputStream(outFileName);
            ObjectOutputStream out = new ObjectOutputStream(fileout);
            out.writeObject(obj);
            out.close();
            fileout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class SER03 {

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.println("Enter your password: ");

        String password = scan.nextLine();

        Encryption e1 = new Encryption();
        Serialize s1 = new Serialize();

        String encryptedPassword = e1.encrypt(password);
        s1.serializeData("Shalom/Rules/SER03.txt", encryptedPassword);
        System.out.println("Encrypted password saved to SER03.txt");
    }

}
