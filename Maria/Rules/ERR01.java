package Maria.Rules;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
/**Rule 07. ERR01-J: Do not allow exceptions to expose sensitive information 
 * @author Maria Plascencia 
 */
public class ERR01 {
        //Main class method
        public static void main(String[] args) { 
        //Try-catch to "open" file and catch exception if file is not found.
        try { 
                //Creating file input stream object to read file.
                FileInputStream fileInput = new FileInputStream("rules.txt"); 
        } catch (FileNotFoundException e) { 
                //Printing error message without exception information 
                System.out.println("File not found.");
        } 
        } 
} 