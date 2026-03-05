package Azul.Rules;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Demonstrates FIO50-J: Do not make assumptions about file creation.
 * This class creates a file with the {@code CREATE_NEW} option to ensure
 * that a new file is only created if it does not already exist.
 */

public class FIO50{

    /**
     * The main method calls createFile(String) to create "file.txt"
     * and handles any IOException.
     * @param args command-line arguments (not used)
     */
	public static void main(String[] args){
	try{
		createFile("file.txt");
		System.out.println("File created!");
	}catch (IOException e){
		System.err.println("Failed to create: " + e.getMessage());
	}

	}

    /**
     * Creates a file with the given name using StandardOpenOption#CREATE_NEW,
     * which fails if the file already exists.
     * @param filename the name of the file to create
     * @throws IOException if an I/O error occurs or the file already exists
     */
		public static void createFile(String filename) throws IOException{
			try (OutputStream out = new BufferedOutputStream(
				Files.newOutputStream(Paths.get(filename), StandardOpenOption.CREATE_NEW))) {
					out.write("File\n".getBytes());

			} catch (IOException x){
				throw new IOException("Error creating file", x);

			}
			
		}
}
