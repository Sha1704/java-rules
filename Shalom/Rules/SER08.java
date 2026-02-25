package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * RULE SER08-J: Do not deserialize data 
 * (convert a saved data to a usable object) 
 * with full permission, restrict the permission 
 * of the data before converting into an object to 
 * prevent attackers from messing with the system.
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
    @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace", "UseSpecificCatch"})
    public static void main(String[] args) {

        try {

            Student s1 = new Student();

            FileOutputStream outputStream = new FileOutputStream("Shalom/Rules/SER08.txt");
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(s1);
            out.close();

            FileInputStream inputStream = new FileInputStream("Shalom/Rules/SER08.txt");
            ObjectInputStream in = new ObjectInputStream(inputStream);

            // allow only Student class
            ObjectInputFilter filter = ObjectInputFilter.Config.createFilter("SER08$Student;!*");
            in.setObjectInputFilter(filter);

            Student s2 = (Student) in.readObject();
            in.close();

            System.out.println("Deserialized student: " + s2.name);

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
