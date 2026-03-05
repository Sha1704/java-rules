import java.util.Scanner;

public class OBJ54 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter input: ");
        String input = scanner.nextLine();
        String output = input+" processed";
        System.out.println("Processed output: " + output);
        // Close the scanner
        scanner.close();
        //then don't assign scanner to null or use it again after closing
        
    }

}
