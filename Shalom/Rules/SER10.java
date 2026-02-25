/**
 * Demonstrates safe serialization of a String object.
 */

package Shalom.Rules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Main class to serialize a String object.
 */
public class SER10 {

    /**
     * Main method to serialize a String.
     * @param args command-line arguments
     */
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
