package Shalom.Example;

/**
 * Demonstrates deserialization of an Emp object from a file.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Main class to deserialize an Emp object.
 */
public class DeserializationClass {

    /**
     * Main method to deserialize an Emp object and print its fields.
     * @param args command-line arguments
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be found
     */
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Emp emp = null;
        try {
            FileInputStream filein = new FileInputStream("Shalom/Example/SerializationClassTest.txt");
            ObjectInputStream in = new ObjectInputStream(filein);
            emp = (Emp) in.readObject();
            in.close();
            filein.close();
        } finally {
            System.out.println("Deserializing Employee...");
            System.out.println("Name of Employee: " + emp.name);
            System.out.println("Address of employee: " + emp.address);
            System.out.println("Age of employee: " + emp.age);
        }
    }
}
