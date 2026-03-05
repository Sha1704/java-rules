import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDS01 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a filename: ");
        String userInput = scanner.nextLine();

        // Normalize
        String normalized = Normalizer.normalize(userInput, Form.NFKC);

        // Validate 
        Pattern allowed = Pattern.compile("^[A-Za-z0-9_.]+$");
        Matcher matcher = allowed.matcher(normalized);

        if (!matcher.matches()) {
            scanner.close();
            throw new IllegalArgumentException("Invalid filename");

        }

        System.out.println("Safe to use: " + normalized);
        scanner.close();
    }
}