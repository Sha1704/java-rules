package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * RULE SER09-J: This rule states that an overridable method 
 * must not be called in a readObject() method because the 
 * object can be initialized/changed by an attacker to run malicious code.
 */
public class SER09 {

/**
 * Serializable Student class with validation in readObject.
 */
    static class Student implements Serializable {

        @SuppressWarnings("FieldMayBeFinal")
        private String name = "Major Problem";

        /**
         * Custom deserialization with validation.
         * @param in the ObjectInputStream
         * @throws IOException if an I/O error occurs
         * @throws ClassNotFoundException if the class cannot be found
         */
        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {

            in.defaultReadObject();

            if (name == null) {
                throw new InvalidObjectException("name must not be null");
            }
        }
    }

    /**
     * Main method to serialize and deserialize a Student object.
     * @param args command-line arguments
     */
    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch", "ConvertToTryWithResources"})
    public static void main(String[] args) {

        try {

            Student s = new Student();

            FileOutputStream outputStream = new FileOutputStream("Shalom/Rules/SER09.txt");
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(s);
            out.close();

            FileInputStream inputStream = new FileInputStream("Shalom/Rules/SER09.txt");
            ObjectInputStream in = new ObjectInputStream(inputStream);
            Student s2 = (Student) in.readObject();
            System.out.println("Student: " + s2.name);
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
