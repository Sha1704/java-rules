package Azul.Rules;

//Rule 13 FIO02-J
//detect and handle file-related errors

import java.io.File;

/**
 * FIO02-J: Detect and handle file-related errors.
 * This class attempts to delete a file and checks the return value of
 */

public class FIO02{
    /**
     * The main method attempts to delete a file
     * Prints an error message if deletion fails, otherwise confirms success.
     * @param args command-line arguments (not used)
     */
	public static void main(String[] args){
		//change file name depending on file used
		File file = new File("file");
		if(!file.delete()){
			//Deletion failed, handle error
			System.err.println("Error: deletion failed");
		}else{
			System.out.println("File deleted.");
		}
	}
}