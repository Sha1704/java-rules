package app;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SaveFile {
    
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

