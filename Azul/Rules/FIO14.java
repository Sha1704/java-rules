package Azul.Rules;

import java.io.*;

public class FIO14{ 
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