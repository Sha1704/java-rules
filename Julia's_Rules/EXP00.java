public class EXP00 {
    public static void main(String[] args) {
 
            String word = "hello";
            // String.toUpperCase() returns a NEW string.
            // Compliant because we store the returned value.
            word = word.toUpperCase();

            System.out.println(word);  // Prints: HELLO

    }
}
