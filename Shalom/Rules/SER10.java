package Shalom.Rules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * RULE SER010-J: This rule says to call reset() 
 * often to clear the cache and prevent memory leaks 
 * and crashes when serializing many objects at the same time. 
 */

public class SER10 {

    /**
     * Main method to serialize a String.
     * @param args command-line arguments
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {

        try (
                FileOutputStream file = new FileOutputStream("Shalom/Rules/SER10.txt"); ObjectOutputStream out = new ObjectOutputStream(file)) {

            out.writeObject("John is a boy");
            out.flush();
            file.close();

            System.out.println("Object serialized safely");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
