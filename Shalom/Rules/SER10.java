package Shalom.Rules;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SER10 {

    public static void main(String[] args) {

        try (
                FileOutputStream file = new FileOutputStream("Shalom/Rules/SER10.txt"); ObjectOutputStream out = new ObjectOutputStream(file)) {

            out.writeObject("John is a boy");
            out.flush();
            file.close();

            System.out.println("Object serialized safely");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
