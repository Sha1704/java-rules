package app;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
//import javax.crypto.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileManager {

    // FIO02-J: Detect and handle file-related errors
    public void deleteFile(String filename) {
    File file = new File(filename);
    if (file.delete()) {
        System.out.println("File deleted successfully.");
    } else {
        System.out.println("Failed to delete file. It may not exist or be locked.");
    }
}

   
    // FIO14-J: Perform proper cleanup at program termination
    public void cleanUp(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

    // FIO08-J: Distinguish between characters or bytes read from a stream and -1
    public void charByte(String filename) {
        try (InputStream in = Files.newInputStream(Paths.get(filename))) {
            int data;
            while ((data = in.read()) != -1) {
                byte b = (byte) data; 
                System.out.print((char) b);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }


    // FIO50-J: Do not make assumption about file creation
    public void checkFileCreation(String filename) {
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(Paths.get(filename), StandardOpenOption.CREATE_NEW))) {

            System.out.println("File created successfully.");

        } catch (FileAlreadyExistsException e) {
            System.out.println("File already exists.");
        } catch (IOException e) {
            System.out.println("Error creating file.");
        }
    }

    // FIO51-J: Identify file using multiple attributes
    public void checkFileAttributes(String filename) {
        File file = new File(filename);

        if (file.exists() && file.isFile() && file.length() > 0) {
            System.out.println("Valid file detected.");
        } else {
            System.out.println("Invalid or suspicious file.");
        }
    }

    // FIO52-J: Ensure sensitive data is not stored in plain text
public void storePasswordHash_FIO52(String password, String filename) {
    try {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        Files.write(Paths.get(filename), hash);
        System.out.println("Password hash stored in " + filename);
    } catch (NoSuchAlgorithmException | IOException e) {
        System.out.println("Failed to store password hash: " + e.getMessage());
    }
}

    // FIO05-J: Do not expose buffers or their backing arrays methods to untrusted code
    public byte[] checkBuffers(byte[] data) {
        if (data == null) return null;
        return Arrays.copyOf(data, data.length); 
    }

}