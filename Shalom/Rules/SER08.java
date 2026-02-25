package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Demonstrates serialization and deserialization of a simple immutable class.
 */
public class SER08 {

    static final class Student implements Serializable {

        /** Name of the student. */
        String name = "John Doe";
    }

    /**
     * Main method to serialize and deserialize a Student object.
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        try {

            Student s1 = new Student();

            FileOutputStream outputStream = new FileOutputStream("Shalom/Rules/SER08.txt");
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(s1);
            out.close();

            FileInputStream inputStream = new FileInputStream("Shalom/Rules/SER08.txt");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            Student s2 = (Student) in.readObject();
            in.close();

            System.out.println("Deserialized student: " + s2.name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
