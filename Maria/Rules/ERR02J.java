package Maria.Rules;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rule 07. ERR02-J: Prevent exceptions while logging data 
 * @author Maria Plascencia
 */
public class ERR02J {

    //Creating a logger object to log any exceptions that may occur
    private static final Logger logger = Logger.getLogger(ERR02J.class.getName());

    //Main class method 
    public static void main(String[] args) {
        //Simple try-catch to catch exceptions
        try {
            //Example error
            throw new Exception("Example creates an error.");

        } catch (Exception e) {
            //Making sure logging still happens even if an exception occurs during logging.
            try {
                logger.log(Level.SEVERE, "Error occurred", e);
            } catch (Exception logError) {
                System.out.println("Logging failed safely.");
            }
            System.out.println("Program continues safely.");
        }
    }
}
