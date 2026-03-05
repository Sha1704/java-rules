package Azul.Rules;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * FIO51-J: Identify files using multiple attributes.
 * This class uses a file's path to open it and perform read/write operations,
 */

public class FIO51{
    /**
     * The main method calls processFile(String) on "file.txt".
     * @param args command-line arguments
     * @throws IOException if an I/O error occurs
     */
	public static void main(String[] args) throws IOException{
		processFile("file.txt");
	}

    /**
     * Opens a file for random access, writes "Hello World", then reads it back
     * and prints the content. Showing basic file identification by path.
     * @param filename the path of the file to process
     * @throws IOException if an I/O error occurs
     */
		public static void processFile(String filename) throws IOException{
			//identify a file by its path
			try(RandomAccessFile file = new RandomAccessFile(filename, "rw")){

				//Write to file
				file.writeBytes("Hello World");

				//go back to begining and read contents
				file.seek(0);
				String line;
				while((line = file.readLine()) != null){
					System.out.println(line);
				}
			}
		}		
		
}

