package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Date;
import java.util.Scanner;

import javax.crypto.SecretKey;

/**
 * Main class that runs the Blackjack application.
 * Handles user interaction, database access, and game flow.
 * 
 * @author Julia Everett
 */
public class Main {

    /**
     * Prints the available options during the Blackjack game.
     * Allows the player to either hit or stay.
     */

    // SHALOM
    // Add cryptographic key fields for save/load
    // In production, load/generate keys securely and persist them
    private static javax.crypto.SecretKey encryptionKey;
    private static java.security.PrivateKey signatureKey;
    private static java.security.PublicKey verificationKey;
    private static SaveFile saveFile = new SaveFile();
    public static void printGameOptions() {
        System.out.println("Choose a play option:");
        System.out.println("1. Hit");
        System.out.println("2. Stay");
        System.out.print("Enter your choice: ");
        System.out.println();
    }

    /**
     * Prints the main menu options available to the user.
     * Includes playing the game, adding money, viewing balance, or exiting.
     */
    public static void printOptions(Regular regularPlayer) {
        System.out.println("Choose an action:");
        System.out.println("1. Play");
        System.out.println("2. Add money");
        System.out.println("3. View balance");
        System.out.println("4. Exit");
        // SHALOM
        System.out.println("5. Save player profile");
        System.out.println("6. Load player profile");
        
   
        if(regularPlayer.isEligibleForVIP()){
            System.out.println("7. VIP Promotion");
        }
        System.out.print("Enter your choice: ");
       
    }

    public static void printVIPOptions() {
        System.out.println("Choose a regular account option:");
        System.out.println("1. Select Perk");
        System.out.println("2. View runtime.exec command");
        System.out.print("Enter your choice: ");
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
    



    /**
     * Retrieves the account balance for a specific user from the database.
     *
     * @param conn - database connection
     * @param id - user account id
     * @return - the account balance if found, otherwise -1
     * @throws SQLException if database query fails
     */
    public static double getBalanceFromDB(Connection conn, int id) throws SQLException {

        String sql = "SELECT balance FROM account_balance WHERE id = ?";
        id = validateId(id);
        if(id <= 0) {
            throw new IllegalArgumentException("Invalid ID.");
        }
        try (PreparedStatement state = conn.prepareStatement(sql)) {

            state.setInt(1, id);

            try (ResultSet rs = state.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }

        return -1.0;
    }

    /**
     * Updates the user's name in the database.
     *
     * @param conn - database connection
     * @param id - user account id
     * @param newName - new name to store
     * @return - true if update was successful, false otherwise
     * @throws SQLException if update fails
     */
    public static boolean updateNameInDB(Connection conn, int id, String newName) throws SQLException {

        String sql = "UPDATE account_balance SET name = ? WHERE id = ?";
        newName = vaildateString(newName);
        id = validateId(id);

        try (PreparedStatement state = conn.prepareStatement(sql)) {

            state.setString(1, newName);
            state.setInt(2, id);

            int rowsUpdated = state.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Updates the user's balance in the database.
     *
     * @param conn - database connection
     * @param id - user account id
     * @param newBalance - updated balance amount
     * @return - true if update succeeded, false otherwise
     * @throws SQLException if update fails
     */
    public static boolean updateBalanceInDB(Connection conn, int id, double newBalance) throws SQLException {

        String sql = "UPDATE account_balance SET balance = ? WHERE id = ?";
        id = validateId(id);
        newBalance = validateBalance(newBalance);

        try (PreparedStatement state = conn.prepareStatement(sql)) {

            state.setDouble(1, newBalance);
            state.setInt(2, id);

            int rowsUpdated = state.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Validates and normalizes a user provided string.
     * Ensures the string contains only allowed characters and is within length limits.
     *
     * @param str - input string
     * @return - validated and normalized string
     */
    public static String vaildateString(String str) {

        str = str.trim();
        String normalized = Normalizer.normalize(str, Form.NFKC);

        if (normalized == null || normalized.length() > 50) {
            throw new IllegalArgumentException("Invalid name length.");
        }

        if (!normalized.matches("[A-Za-z0-9_ ]+")) {
            throw new IllegalArgumentException("Invalid characters in name.");
        }

        return normalized;
    }

    /**
     * Validates the account ID.
     * Ensures the ID is greater than zero.
     *
     * @param id - account id
     * @return - validated id
     */
    public static int validateId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ID.");
        }

        return id;
    }

    /**
     * Validates the account balance.
     * Ensures the balance is within an acceptable range.
     *
     * @param balance - balance value
     * @return - validated balance
     */
    public static double validateBalance(double balance) {

        if (balance < 0 && balance > 1000000) {
            throw new IllegalArgumentException("Invalid balance.");
        }

        return balance;
    }

    /**
     * Runs the Blackjack gameplay loop.
     * Handles betting, card dealing, player decisions, and determining the winner.
     *
     * @param scanner - scanner used for user input
     * @param gamePlayer - the player participating in the game
     * @param regularPlayer - the regular player instance
     * @param conn - database connection
     */
    public static void playGame(Scanner scanner, Player gamePlayer, Regular regularPlayer, Connection conn) throws SQLException {

        boolean keepPlaying = true;

        Dealer house = new Dealer(new Deck());
    
        int gamesPlayed = 0;

        while (keepPlaying) {

            String playChoice = "";
            if(regularPlayer.isEligibleForVIP()){
                System.out.println("As a VIP member, you receive a perk go to regular options to select one!");
                
            }

            System.out.print("Place your bet to start play: ");
            double betAmount = scanner.nextDouble();
            scanner.nextLine();

            if (betAmount > 0) {

                boolean playerWon = true;
                

                // Deduct bet from player's balance
                boolean goodBet = gamePlayer.placeBet(betAmount);
                if(goodBet) {
                    
               
                regularPlayer.playGame(betAmount);
                boolean betUpdated = updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance()-betAmount);
                if(!betUpdated) {
                    System.out.println("Failed to update balance in database.");
                }


                house.addPlayer(gamePlayer);
                gamePlayer.setActive(true);
                
                house.startNewRound();
                boolean playerTurn = true;
                Hand currentHand = gamePlayer.getHand();
                while (playerTurn && currentHand.getValue() <= 21) {

                    System.out.println("Player's hand:");

                    for (int i = 0; i < currentHand.getCards().size(); i++) {
                        System.out.println(
                                "Card " + (i + 1) + ": " + currentHand.getCards().get(i));
                    }

                    System.out.println("Hand value: " + currentHand.getValue());

                    printGameOptions();
                    playChoice = scanner.nextLine().trim();

                    switch (playChoice) {

                        case "1":
                            System.out.println("Player chose to HIT.");
                            gamePlayer.hit();
                            break;

                        case "2":
                            System.out.println("Player chose to STAY.");
                            gamePlayer.stand();
                            playerTurn = false;
                            break;

                        default:
                            System.out.println("INVALID CHOICE.");
                    }
                    house.processPlayerTurns();
                }
                house.playTurn();
                house.settleBets();
                
                // Display final hand values
                System.out.println("Dealer's hand value is "
                        + house.getHandValue()
                        + ", player's hand value is "
                        + currentHand.getValue() + ".");

                // Determine winner
                if (house.getHandValue() > currentHand.getValue()
                        && house.getHandValue() <= 21) {
                   
                    playerWon = false;

                } 
                else if (currentHand.getValue() > 21) {
                    playerWon = false;
                    if(house.getHandValue() > 21) {
                        System.out.println("Both player and dealer busted! It's a tie.");
                        playerWon = false; // Treat as loss for player since they also busted
                        gamePlayer.addWinnings(betAmount);
                    }
                    else{
                        System.out.println("Player busted! Dealer wins.");
                        playerWon = false;
                    }

                    
                    

                }
                else if (house.getHandValue() == currentHand.getValue()
                        && !(house.getHandValue() > 21)) {

                    
                    gamePlayer.addWinnings(betAmount);
                    updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance());
                    if(!betUpdated) {
                        System.out.println("Failed to update balance in database.");
                    }
                    playerWon = false;
                }
              

                house.resetHand();
                house.removePlayer(gamePlayer.getPlayerId());
                gamePlayer.resetForNewRound();

                gamesPlayed++;
                

                System.out.println();

                // Award winnings if player wins
                if (playerWon) {
                    System.out.println("Congratulations! You won this round.");
                    gamePlayer.addWinnings(betAmount * 2);
                    boolean winningsUpdated = updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance());
                    if(!winningsUpdated) {
                        System.out.println("Failed to update balance in database.");
                    }
                } 
                else 
                {
                    System.out.println("Sorry, you lost this round.");
                }

                // Ask player if they want to play again
                System.out.print("Do you want to play again? 1 for yes, 2 for no: ");
                String again = scanner.nextLine().trim();

                if (again.equals("2")) {

                    System.out.println("Exiting game...");
                    keepPlaying = false;

                } else if (again.equals("1")) {

                    System.out.println("Starting a new game...");
                    gamePlayer.resetForNewRound();

                } else {

                    System.out.println("INVALID CHOICE. Returning to menu.");
                    keepPlaying = false;
                }
            

            }} else {

                System.out.println(
                        "Insufficient balance to place bet. Please add more money to your account.");
                keepPlaying = false;
            }
            
        }

    }

    /**
     * Main method that launches the Blackjack program.
     * Connects to the database, authenticates the user, 
     * and displays the main menu options.
     *
     * @param args - command line arguments
     */
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Blackjack Game!");

        boolean validAccount = false;
        Connection conn = null;

        String DB_URL = "jdbc:oracle:thin:@10.110.10.90:1521:oracle";
        String USER = "IT326S09";
        String PASS = "pink22";

        String name = "";
        int id = -1;

        // SHALOM
        try {
            // Generate AES key for encryption
            encryptionKey = EncryptAndDecrypt.generateKey("AES");

            // Generate RSA key pair for signing/verification
            java.security.KeyPairGenerator kpg = java.security.KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            java.security.KeyPair kp = kpg.generateKeyPair();
            signatureKey = kp.getPrivate();
            verificationKey = kp.getPublic();
        } catch (Exception e) {
            System.err.println("Key generation error: " + e.getMessage());
        }
        try {

            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Authenticate user
            while (!validAccount) {

                System.out.print("Please enter your name:");
                name = scanner.nextLine();

                System.out.print("Please enter your id:");
                id = scanner.nextInt();
                scanner.nextLine();

                validAccount = updateNameInDB(conn, id, name);

                if (!validAccount) {

                    System.out.println("Invalid name or id, please try again.");
                    System.out.println();
                }
            }

            double currBalance = getBalanceFromDB(conn, id);

            Player gamePlayer = new Player(id, name, currBalance);
            Regular regularPlayer = new Regular(name, currBalance);

            String choice = "";

            System.out.println("Your current balance is: $" + currBalance);

            // Main menu loop
            while (!choice.equals("4")) {

                System.out.println();
                System.out.println("Hello " + name + ", what would you like to do?");

                printOptions(regularPlayer);
                choice = scanner.nextLine().trim();

                switch (choice) {

                    case "1":

                        System.out.println("chose to PLAY.");
                        playGame(scanner, gamePlayer, regularPlayer, conn);

                        break;

                    case "2":

                    System.out.println("chose to ADD MONEY.");

                    System.out.print("Enter amount to add: ");
                    double amountToAdd = scanner.nextDouble();

                    boolean changedAmount =
                            updateBalanceInDB(conn, id, currBalance);
                    regularPlayer.addBalance( amountToAdd);
                    gamePlayer.addMoney(amountToAdd);
                    

                    if (changedAmount) {

                        System.out.println("Balance updated successfully."
                                + " New balance: $"
                                + getBalanceFromDB(conn, id));

                    } else {

                        System.out.println("Failed to update balance.");
                    }

                    gamePlayer.addMoney(amountToAdd);

                    scanner.nextLine();
                    break;

                case "3":

                    System.out.println("chose to VIEW BALANCE.");
                    System.out.println("Your current balance is: $"
                            + gamePlayer.getChipBalance());
                    break;

                case "4":

                    System.out.println("chose to EXIT.");
                    break;
// SHALOM
                case "5":
                    System.out.println("chose to SAVE PLAYER PROFILE.");
                    if (encryptionKey == null || signatureKey == null) {
                        System.err.println("Encryption or signature key not initialized.");
                    } else {
                        boolean saved = saveFile.savePlayer(gamePlayer, encryptionKey, signatureKey);
                        if (saved) {
                            System.out.println("Player profile saved.");
                        } else {
                            System.out.println("Failed to save player profile.");
                        }
                    }
                    break;
                case "6":
                    System.out.println("chose to LOAD PLAYER PROFILE.");
                    if (encryptionKey == null || verificationKey == null) {
                        System.err.println("Encryption or verification key not initialized.");
                    } else {
                        Player loadedPlayer = saveFile.loadPlayer(encryptionKey, verificationKey);
                        if (loadedPlayer != null) {
                            gamePlayer = loadedPlayer;
                            System.out.println("Player profile loaded.");
                        } else {
                            System.out.println("Failed to load player profile.");
                        }
                    }
                    break;// END SHALOM
                case "7":
                    if(regularPlayer.isEligibleForVIP()){
                        Date lastLogin = new Date();
                        regularPlayer= new VIP(regularPlayer.getPlayerName(), regularPlayer.getBalance(), regularPlayer.getGamesPlayed(),lastLogin); 
                        VIP vipPlayer = new VIP(regularPlayer.getPlayerName(), regularPlayer.getBalance(), regularPlayer.getGamesPlayed(),lastLogin); 
                        System.out.println("Congratulations! You are eligible for our VIP promotion!");
                        printVIPOptions();
                        String vipChoice = scanner.nextLine();
                        

                        if(vipChoice.equals("1")){
                            System.out.print("Select a perk from the available options: " + vipPlayer.getAvailablePerks() + ": ");
                            String perkChoice = scanner.nextLine();
                            vipPlayer.selectPerk(perkChoice);
                        }else if(vipChoice.equals("2")){
                            String os = System.getProperty("os.name").toLowerCase();
                            boolean isWindows = os.contains("win");

                            //for rule IDS07
                            System.out.println("Choose a directory listing option:");
                            System.out.println("1. Basic listing");
                            System.out.println("2. Show hidden files");
                            System.out.println("3. Detailed listing");
                            System.out.print("Enter your choice: ");

                            int choiceCommand = scanner.nextInt();

                            // Build a trusted command based on OS
                            String[] command = buildCommand(choiceCommand, isWindows);


                            // Execute the trusted command safely
                            Process process = Runtime.getRuntime().exec(command);
                            int exitCode = process.waitFor();

                            if (exitCode != 0) {
                                System.out.println("Command failed with exit code " + exitCode);
                            } else {
                                process.getInputStream().transferTo(System.out);
                            }
                        }
                        else {
                            System.out.println("INVALID CHOICE.");
                        }
                    } else {
                        System.out.println("Sorry, you are not eligible for the VIP promotion yet. Keep playing to unlock it!");
                    }
                    break;    
                default:

                    System.out.println("INVALID CHOICE.");
                }

                System.out.println();
                
            }

            scanner.close();

        } catch (SQLException e) {

            System.err.format("SQL State: %s\n%s",
                    e.getSQLState(),
                    e.getMessage());

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            // Close database connection
            if (conn != null) {

                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
    }
}
