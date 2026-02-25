package Shalom.Rules;

/**
 * RULE SER06-J: When deserializing a mulabel (changeable) data 
 * like an array or a date,  make sure to make a copy of the data 
 * before deserializing it so an attacker can't change the state of 
 * the data to do whatever they want.
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * Serializable Student class with defensive copying of Date.
 */
class Student implements Serializable {

    private Date enrollmentDate;

    /**
     * Constructs a Student with the given enrollment date.
     * @param date the enrollment date
     */
    public Student(Date date) {
        this.enrollmentDate = new Date(date.getTime());
    }

    /**
     * Returns a defensive copy of the enrollment date.
     * @return the enrollment date
     */
    public Date getEnrollmentDate() {
        return new Date(enrollmentDate.getTime());
    }

    /**
     * Custom deserialization to ensure defensive copy of Date.
     * @param in the ObjectInputStream
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be found
     */
    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (enrollmentDate != null) {
            enrollmentDate = new Date(enrollmentDate.getTime());
        }
    }
}

/**
 * Main class to demonstrate defensive copying in serialization.
 */
public class SER06 {

    /**
     * Main method to serialize and deserialize a Student object.
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        try {

            Student s1 = new Student(new Date());

            System.out.println("Original enrollment date: " + s1.getEnrollmentDate());

            FileOutputStream output = new FileOutputStream("Shalom/Rules/SER06.txt");
            ObjectOutputStream out = new ObjectOutputStream(output);
            out.writeObject(s1);
            out.close();

            FileInputStream input = new FileInputStream("Shalom/Rules/SER06.txt");
            ObjectInputStream in = new ObjectInputStream(input);
            Student s2 = (Student) in.readObject();
            in.close();

            System.out.println("Deserialized enrollment date: " + s2.getEnrollmentDate());

            Date leakedDate = s2.getEnrollmentDate();
            leakedDate.setTime(0);

            System.out.println("After external modification attempt: " + s2.getEnrollmentDate());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
