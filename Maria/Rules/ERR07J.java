package Maria.Rules;

/**
 * Rule 07. ERR07-J: Do not throw RuntimeException, Exception, or Throwable.
 * Shows exception ERR07-J-EX0: Allows throwing a general exception when needed to comply with a security policy.
 * @author Maria Plascencia
 */
public class ERR07J {

    // Method throws an SPECIFIC exception instead of a general one.
    public static void checkValue(int value) throws IllegalArgumentException {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative.");
        }
    }
    // Example of exception (ERR07-J-EX0). Would do this if we needed to only due to SECURITY reasons.
    public static void sanitizeExample() throws Exception {
        try {
            // Original specific exception.
            throw new IllegalArgumentException("Sensitive details");
        } catch (IllegalArgumentException e) {
            // General exception hides sensitive information.
            throw new Exception("General error occurred.");
        }
    }
    // Main class method
    public static void main(String[] args) {
        //Simple try-catch to catch exceptions. 
        try {
            checkValue(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught exception: " + e.getMessage());
        }
        // Try-catch to show sanitized exception example.
        try {
            sanitizeExample();
        } catch (Exception e) {
            System.out.println("Caught sanitized exception: " + e.getMessage());
        }

    }
}

