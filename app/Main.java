package app;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for money input
        System.out.print("Enter money into your account: ");
        String money = scanner.nextLine().trim();


        String choice = "";

        while (!choice.equals("3")) {
            System.out.println("Welcome to Blackjack, your $" + money + "!");
            System.out.println("Choose an action:");
            System.out.println("1. Hit");
            System.out.println("2. Stay");
            System.out.println("3. Exit");
            System.out.print("Enter your choice (1-3): ");
           
            choice = scanner.nextLine().trim();
    
            switch (choice) {
                case "1":
                    System.out.println( " chose to HIT.");
                    break;
                case "2":
                    System.out.println( " chose to STAY.");
                    break;
                case "3":
                    System.out.println( " chose to EXIT.");
                    break;
                default:
                    System.out.println("Invalid choice. Please restart the program.");
            }
        }

        scanner.close();
    }
}