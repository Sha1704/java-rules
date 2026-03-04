package Shalom.Rules;

/**
 * RULE SER05-J: Do not serialize a non static inner class because it serializes 
 * the outer class and may cause unexpected behavior and errors
 * @author: Adiboshi Shalom
 */

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SER05 {

    private String schoolName = "Illinois state university";

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * Serializable static inner class representing a student.
     */
    static class Student implements Serializable {

        @SuppressWarnings("unused")
        String name;
        @SuppressWarnings("unused")
        int age;

        /**
         * Prints the school name from the outer class.
         * @param outer the outer SER05 instance
         */
        @SuppressWarnings("unused")
        void printSchool(SER05 outer) {
            System.out.println("School: " + outer.schoolName);
        }
    }

    /**
     * Main method to serialize a Student object.
     * @param args command-line arguments
     */
    @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace"})
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
