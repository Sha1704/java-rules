package Azul.Rules;

import java.io.*;

/**
 * FIO14-J: Perform proper cleanup at program termination.
 * This class uses a {@code finally} block to ensure a stream is closed
 * even if an exception occurs or the program exits abnormally.
 */

public class FIO14{ 
    /**
     * The main method writes a line to "foo.txt" and ensures the
     * {@code PrintStream} is closed 
     * @param args command-line arguments (not used)
     * @throws FileNotFoundException if the file cannot be opened
     */
	public static void main(String[] args) throws FileNotFoundException{
		final PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("foo.txt")));

		try{
			out.println("hello");
		}finally{
			try{
				out.close();
			} catch (Exception e){
				System.out.println("Error closing file");
			}
		}
		Runtime.getRuntime().exit(1);
	}
}