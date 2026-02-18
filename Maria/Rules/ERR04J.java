package Maria.Rules;

/**
 * Rule 07. ERR04-J: Do not complete abruptly from a finally block.
 * Shows exception ERRO4-J-EX0: Allows complete abruptly from a 
 * control flow statement in a finally block.
 */
public class ERR04J {
    //Main class method
    public static void main(String[] args) throws Exception {
        //try-finally to catch exeptions. 
        try {
            //Example exeption
            throw new Exception("Example creates an exception.");
        } finally {
            // Break allowed inside a control flow statement in a finally block.
            for (int i = 0; i < 1; i++) {
                System.out.println("Inside loop in finally");
                break; 
            }
            //Showing that the finally block completes safely. 
            System.out.println("Finally block ended safely.");

        }
    }

}