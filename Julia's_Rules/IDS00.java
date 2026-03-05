import java.util.Scanner;

public class IDS00 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter product ID: ");
        String productId = scanner.nextLine();
        scanner.close();
        lookupProduct(productId);
    }

    //demonstrates parameterized query safely (simulation only)
    public static void lookupProduct(String productId) {
        // Simulated SQL with placeholder
        String sql = "SELECT name, price FROM products WHERE id = ?";

        System.out.println("\n--- Simulated SQL Execution ---");
        System.out.println("Prepared SQL: " + sql);
        System.out.println("Binding parameter 1 to value: " + productId);

        // Simulated result
        if ("123".equals(productId)) {
            System.out.println("\nProduct Name: Example Widget");
            System.out.println("Price: $19.99");
        } else {
            System.out.println("\nNo product found.");
        }
    }
}