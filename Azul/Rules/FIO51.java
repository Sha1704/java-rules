package Azul.Rules;

import java.io.RandomAccessFile;
import java.io.IOException;

public class FIO51{
	public static void main(String[] args) throws IOException{
		processFile("file.txt");
	}
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

