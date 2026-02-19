package Maria.Rules;

/**
 * Rule 08. VNA00-J: Ensure visibility when accessing shared primitive variables 
 * @author Maria Plascencia
 */
public class VNA00 {
    // Shared variable is declared volatile
    private static volatile int sharedCounter = 0;
    public static void main(String[] args) {
        // Thread 1 updates the shared variable
        new Thread(() -> sharedCounter = 5).start();
        // Thread 2 reads the shared variable with correct value
        new Thread(() -> System.out.println("Shared counter: " + sharedCounter)).start();
    }
}