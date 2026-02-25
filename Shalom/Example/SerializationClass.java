package Shalom.Example;

/**
 * Demonstrates serialization of an Emp object to a file.
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Main class to serialize an Emp object.
 */
public class SerializationClass {

    /**
     * Main method to serialize an Emp object.
     * @param args command-line arguments
     */
    @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace"})
    public static void main(String[] args) {
        Emp emp = new Emp();
        emp.name = "john doe";
        emp.address = "123 over there st";
        emp.age = 69;

        try {
            FileOutputStream fileout = new FileOutputStream("Shalom/Example/SerializationClassTest.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileout);
            out.writeObject(emp);
            out.close();
            fileout.close();
            System.out.println("Serialized data is saved in SerializationClassTest.txt file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
