import java.util.Scanner;

public class EXP03 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter two integer values:");
        System.out.print("First number: ");
        Integer a = Integer.valueOf(scanner.nextInt());

        System.out.print("Second number: ");
        Integer b = Integer.valueOf(scanner.nextInt());


        System.out.println("Using equals() for comparison:");
        if (a.equals(b)) {
            System.out.println("a.equals(b) : TRUE (value comparison)");
        } else {
            System.out.println("a.equals(b) : FALSE (value comparison)");
        }

        scanner.close();
    }

}
