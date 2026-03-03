package app;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The FileManager class provides file operations with a focus on secure coding practices.
 * It includes methods for deleting, writing, reading, creating, and validating files,
 * as well as handling sensitive data like passwords. Each method addresses specific security concerns related to file I/O operations.
 */

public class FileManager {

    /**
     * Deletes a file if it exists and is not locked.
     * FIO02-J: Detect and handle file-related errors
     * @param filename the path of the file to delete
     */
    public void deleteFile(String filename) {
    File file = new File(filename);
    if (file.delete()) {
        System.out.println("File deleted successfully.");
    } else {
        System.out.println("Failed to delete file. It may not exist or be locked.");
    }
}

   /**
     * Writes content to a file and ensures proper cleanup of resources.
     * FIO14-J: Perform proper cleanup at program termination
     * @param filename the path of the file to write
     * @param content  the content to write into the file
     */
    public void cleanUp(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.out.println("Error writing file.");
        }
    }

        /**
     * Reads a file byte by byte and prints each character, distinguishing between
     * a valid byte read and the end-of-stream indicator (-1).
     * FIO08-J: Distinguish between characters or bytes read from a stream and -1 (end of stream)
     * @param filename the path of the file to read
     */
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

    
        /**
     * Creates a new file only if it does not already exist, avoiding accidental overwrites.
     * FIO50-J: Do not make assumption about file creation
     * @param filename the path of the file to create
     */
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

        /**
     * Validates a file by checking its existence, type, and non-zero length.
     * FIO51-J: Identify file using multiple attributes
     * @param filename the path of the file to validate
     */
    public void checkFileAttributes(String filename) {
        File file = new File(filename);

        if (file.exists() && file.isFile() && file.length() > 0) {
            System.out.println("Valid file detected.");
        } else {
            System.out.println("Invalid or suspicious file.");
        }
    }

    /**
     * Stores a password securely by hashing it  and writing the hash to a file.
     * FIO52-J: Ensure sensitive data is not stored in plain text
     * @param password the plaintext password to hash
     * @param filename the path of the file where the hash will be stored
     */
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

        /**
     * Returns a defensive copy of the input byte array to prevent exposure of the internal buffer.
     * FIO05-J: Do not expose buffers or their backing arrays methods to untrusted code
     * @param data the original byte array 
     * @return a new byte array containing a copy of the original, or null if input was null
     */
    public byte[] checkBuffers(byte[] data) {
        if (data == null) return null;
        return Arrays.copyOf(data, data.length); 
    }

}