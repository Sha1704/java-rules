package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SER09 {

    static class Student implements Serializable {

        private String name = "Major Problem";

        private void printInfo() {
            System.out.println("Student: " + name);
        }

        private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {

            in.defaultReadObject();

            if (name == null) {
                throw new InvalidObjectException("name must not be null");
            }
        }
    }

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
