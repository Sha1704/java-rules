import java.util.Scanner;

public class IDS16 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter quantity: ");
        String userInput = scanner.nextLine();

        try {
            String xml = createSafeXML(userInput);
            System.out.println("\nGenerated XML:");
            System.out.println(xml);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity: must be a non-negative integer.");
        }
        scanner.close();
    }

    // validate before constructing XML
    public static String createSafeXML(String quantity) throws NumberFormatException {
        
        // validate that quantity is an unsigned integer
        int count = Integer.parseUnsignedInt(quantity);

  
        return "<item>\n"
                + "  <description>Widget</description>\n"
                + "  <price>500.0</price>\n"
                + "  <quantity>" + count + "</quantity>\n"
                + "</item>";
    }
}