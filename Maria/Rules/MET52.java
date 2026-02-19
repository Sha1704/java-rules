package Maria.Rules;

/**
 * Recommendation 06. MET 52-J: Do not use the clone() method to copy untrusted method parameters 
 * @author Maria Plascencia
 */
public class MET52 {
    public static void main(String[] args) {
        // Original object (simulating untrusted input)
        UserData data = new UserData(42);
        // Safe defensive copy
        UserData safeCopy = new UserData(data);
        System.out.println("Processing value: " + safeCopy.value);
    }
}
/**
 * Helper class representing user data.
 */
class UserData {
    int value;
    /**
     * Constructor to initialize value.
     * @param value
     */
    UserData(int value) {
        this.value = value;
    }
    /**
     * Copy constructor to create a defensive copy of UserData.
     * @param other
     */
    UserData(UserData other) {
        this.value = other.value;
    }
}