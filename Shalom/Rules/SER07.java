package Shalom.Rules;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/*
 * RULE SER07-J: Always manually validate objects after 
 * deserialization because deserilization can create an 
 * object without calling its constructor which is dangerous.
 */
class BankAccount implements Serializable {

    /* Stores the account balance. Must always be >= 0 */
    private int balance;

    /*
     * Constructs a BankAccount with the specified balance.
     * @param balance the initial balance
     * @throws IllegalArgumentException if balance is negative
     */
    public BankAccount(int balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    /*
     * Returns the current balance.
     * @return the account balance
     */
    public int getBalance() {
        return balance;
    }

    /*
     * Deposits money into the account.
     * @param amount the amount to deposit
     * @throws IllegalArgumentException if amount is not positive
     */
    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    /*
     * Custom deserialization method to validate object state.
     * This method ensures that the balance remains valid after
     * deserialization. Since constructors are not called during
     * deserialization, validation must be done here.
     * @param in the ObjectInputStream used to deserialize
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if class cannot be found
     * @throws InvalidObjectException if balance is invalid
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Perform default deserialization
        in.defaultReadObject();

        // Validate invariant
        if (balance < 0) {
            throw new InvalidObjectException("Invalid balance: cannot be negative");
        }
    }
}


/*
 * Main class to demonstrate secure serialization and deserialization.
 * This class serializes a BankAccount object to a file and then
 * deserializes it safely while ensuring validation is enforced.
 */
public class SER07 {

    /*
     * Entry point of the program.
     * @param args command-line arguments (not used)
     */
    @SuppressWarnings({"ConvertToTryWithResources", "UseSpecificCatch", "CallToPrintStackTrace"})
    public static void main(String[] args) {

        try {

            // Create a valid BankAccount object
            BankAccount account = new BankAccount(1000);

            System.out.println("Original balance: " + account.getBalance());

            // Serialize object to file
            FileOutputStream outputStream = new FileOutputStream("Shalom/Rules/SER07.txt");
            ObjectOutputStream out = new ObjectOutputStream(outputStream);

            out.writeObject(account);
            out.close();

            System.out.println("Serialization successful.");

            // Deserialize object from file
            FileInputStream inputStream = new FileInputStream("Shalom/Rules/SER07.txt");
            ObjectInputStream in = new ObjectInputStream(inputStream);

            BankAccount loadedAccount = (BankAccount) in.readObject();

            in.close();

            System.out.println("Deserialization successful.");

            // Display loaded balance
            System.out.println("Loaded balance: " + loadedAccount.getBalance());

        } catch (Exception e) {

            /*
             * Handles serialization or validation errors.
            */
            System.err.println("Error occurred: " + e.getMessage());

            e.printStackTrace();
        }
    }
}
