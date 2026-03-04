package Azul.Rules;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;


public class FIO52{
	public static void main(String[] args) throws Exception{
		String password = "password";

		//hash instead of raw data
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes());

		Files.write(Path.of("password.hash"), hash);
		System.out.println("Stored");
	
		

	}
}
