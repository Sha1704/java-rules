package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

class Student implements Serializable {

    private Date enrollmentDate;

    public Student(Date date) {
        this.enrollmentDate = new Date(date.getTime());
    }

    public Date getEnrollmentDate() {
        return new Date(enrollmentDate.getTime());
    }

    private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {

        in.defaultReadObject();

        if (enrollmentDate != null) {
            enrollmentDate = new Date(enrollmentDate.getTime());
        }
    }
}

public class SER06 {

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
