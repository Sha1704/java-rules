import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OBJ55 {

    // Long-lived container
    private static final List<Integer> numbers = new ArrayList<>();
    
    private static void calculateAverage() {
   

        int sum = 0;
        for (int n : numbers) {
            sum += n;
        }

        double average = (double) sum / numbers.size();
        System.out.println("Average: " + average);

        // Remove short-lived objects from long-lived container
        numbers.clear();
        System.out.println("Numbers cleared after calculation.");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        

        System.out.println("Type a number to store it. Type 'avg' to calculate the average. Type 'exit' to quit.");

        while (true) {
            System.out.print("enter: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.equalsIgnoreCase("avg")) {
                calculateAverage();
            } else {
                try {
                    int value = Integer.parseInt(input);
                    numbers.add(value);
                    System.out.println("Stored " + value);
                } catch (NumberFormatException e) {
                    System.out.println("Not a number.");
                }
            }
        }

        scanner.close();
    }


}