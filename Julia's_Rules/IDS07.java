import java.util.*;

public class IDS07 {

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Detect OS
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        System.out.println("Choose a directory listing option:");
        System.out.println("1. Basic listing");
        System.out.println("2. Show hidden files");
        System.out.println("3. Detailed listing");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();

        // Build a trusted command based on OS
        String[] command = buildCommand(choice, isWindows);

        if (command == null) {
            System.out.println("Invalid choice");
            scanner.close();
            return;
        }

        // Execute the trusted command safely
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            System.out.println("Command failed with exit code " + exitCode);
        } else {
            process.getInputStream().transferTo(System.out);
        }
        scanner.close();
    }

    private static String[] buildCommand(int choice, boolean isWindows) {
        if (isWindows) {
            // Windows uses cmd.exe built‑ins
            return switch (choice) {
                case 1 -> new String[] {"cmd.exe", "/C", "dir"};
                case 2 -> new String[] {"cmd.exe", "/C", "dir /A"};
                case 3 -> new String[] {"cmd.exe", "/C", "dir /Q"};
                default -> null;
            };
        } else {
            
            return switch (choice) {
                case 1 -> new String[] {"ls"};
                case 2 -> new String[] {"ls", "-a"};
                case 3 -> new String[] {"ls", "-l"};
                default -> null;
            };
        }
    }
}