package Maria.Rules;

/**
 * Rule 07. ERR08-J: Do not catch NullPointerException or any of its ancestors.
 * @author Maria Plascencia
 */
public class ERR08J {

    public static void main(String[] args) { 
        String text = null;
        // Check for null pointer before using the variable to avoid NullPointerException.
        if (text == null) {
            System.out.println("Text is null.");
            
        } else {
            System.out.println(text);
        }
    }
}


