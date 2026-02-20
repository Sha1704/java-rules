import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDS11 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter text: ");
        String userInput = scanner.nextLine();

        String result = filterString(userInput);
        System.out.println("Filtered and validated input: " + result);
        scanner.close();
    }

    public static String filterString(String str) {
        // Normalize first
        String s = Normalizer.normalize(str, Form.NFKC);

        // Replace all noncharacter code points BEFORE validation
        s = s.replaceAll("a", "r");

        // Validate AFTER all modifications
        Pattern pattern = Pattern.compile("<script>");
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            throw new IllegalArgumentException("Invalid input: contains <script> tag");
        }

        return s;
    }
}