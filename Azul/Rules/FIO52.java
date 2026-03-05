package Azul.Rules;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

/**
 * FIO52-J: Ensure sensitive data is not stored in plain text.
 * This class hashes a password and stores the hash instead of
 * the plaintext password.
 */

public class FIO52{
    /**
     * The main method takes a hard-coded password, computes its hash,
     * and writes the hash to "password.hash".
     * @param args command-line arguments
     * @throws Exception if the hashing algorithm is not found or an I/O error occurs
     */
	public static void main(String[] args) throws Exception{
		String password = "password";

		//hash instead of raw data
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes());

		Files.write(Path.of("password.hash"), hash);
		System.out.println("Stored");
	
		

	}
}
