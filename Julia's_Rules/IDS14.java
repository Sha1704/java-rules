import java.util.Scanner;

public class IDS14 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Simulate visible field input
        System.out.print("Enter visible parameter: ");
        String visible = scanner.nextLine();

        // Simulate hidden field input (attacker can modify this too)
        System.out.print("Enter hidden parameter: ");
        String hidden = scanner.nextLine();

        // Sanitize BOTH inputs
        String safeVisible = sanitize(visible);
        String safeHidden = sanitize(hidden);

        System.out.println("\nProcessed Output:");
        System.out.println("Visible Parameter: " + safeVisible);
        System.out.println("Hidden Parameter: " + safeHidden);
        
        scanner.close();
    }

    // Very simple HTML sanitizer for demonstration
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
