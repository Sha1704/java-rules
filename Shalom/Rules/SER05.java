package Shalom.Rules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SER05 {

    private String schoolName = "Illinois state university";

    static class Student implements Serializable {

        String name;
        int age;

        void printSchool(SER05 outer) {
            System.out.println("School: " + outer.schoolName);
        }
    }

    public static void main(String[] args) {
        Student s1 = new Student();

        try {
            FileOutputStream fileout = new FileOutputStream("Shalom/Rules/SER05.txt");
            ObjectOutputStream out = new ObjectOutputStream(fileout);
            out.writeObject(s1);
            out.close();
            fileout.close();
            System.out.println("Serialized data is saved in SER05.txt file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
