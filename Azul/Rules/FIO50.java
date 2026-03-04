package Azul.Rules;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FIO50{
	public static void main(String[] args){
	try{
		createFile("file.txt");
		System.out.println("File created!");
	}catch (IOException e){
		System.err.println("Failed to create: " + e.getMessage());
	}

	}



		public static void createFile(String filename) throws IOException{
			try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(Paths.get(filename), StandardOpenOption.CREATE_NEW))) {
					out.write("File\n".getBytes());

			} catch (IOException x){
				throw new IOException("Error creating file", x);

			}
			
		}
}
