package Azul.Rules;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * FIO08-J: Distinguish between characters or bytes read from a stream and -1.
 * This class reads a file byte by byte and properly checks for the end-of-stream
 * indicator (-1) to avoid misinterpreting valid data.
 */

public class FIO08{
	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		System.out.print("Enter file: ");
		String file = scan.nextLine();

		try{
			FileInputStream in = new FileInputStream(file);
			int buff;
			byte data;

			while((buff = in.read()) != -1){
				data = (byte) buff;
				System.out.println((char) data);
			}
			in.close();
		}catch (IOException e){
			System.out.println("Error: " + e.getMessage());
		}
		scan.close();
	}
}