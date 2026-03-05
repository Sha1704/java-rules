package app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.crypto.SecretKey;

/**
 * Main class that runs the Blackjack application.
 * Handles user interaction, database access, and game flow.
 * 
 * @author Julia Everett
 */
public class Main {

    // SHALOM
    // Add cryptographic key fields for save/load
    // In production, load/generate keys securely and persist them
    private static javax.crypto.SecretKey encryptionKey;
    private static java.security.PrivateKey signatureKey;
    private static java.security.PublicKey verificationKey;
    private static SaveFile saveFile = new SaveFile();

    /**
     * Prints the available options during the Blackjack game.
     * Allows the player to either hit or stay.
     */
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
     *
     * @param regularPlayer the regular player instance to check VIP eligibility
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

    /**
     * Prints the VIP menu options available to eligible players.
     */
    public static void printVIPOptions() {
        System.out.println("Choose a regular account option:");
        System.out.println("1. Select Perk");
        System.out.println("2. View runtime.exec command");
        System.out.print("Enter your choice: ");
    }
    
    /**
     * Builds a safe command based on user choice and operating system.
     * 
     * @param choice - user's menu selection
     * @param isWindows - true if running on Windows
     * @return array of command strings to execute
     */
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
        newName = validateString(newName);
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
    public static String validateString(String str) {
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
        if (balance < 0 || balance > 1000000) {
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
     * @throws SQLException if database operations fail
     */
    public static void playGame(Scanner scanner, Player gamePlayer, Regular regularPlayer, Connection conn) throws SQLException {
        boolean keepPlaying = true;
        Dealer house = new Dealer(new Deck());

        while (keepPlaying) {
            if(regularPlayer.isEligibleForVIP()){
                System.out.println("As a VIP member, you receive a perk! Go to regular options to select one.");
            }

            System.out.print("Place your bet to start play: $");
            double betAmount = scanner.nextDouble();
            scanner.nextLine();

            if (betAmount > 0 && betAmount <= gamePlayer.getChipBalance()) {
                boolean playerWon = false;

                // Place bet
                boolean goodBet = gamePlayer.placeBet(betAmount);
                if(goodBet) {
                    regularPlayer.playGame(betAmount);
                    boolean betUpdated = updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance());
                    if(!betUpdated) {
                        System.out.println("Failed to update balance in database.");
                    }

                    house.addPlayer(gamePlayer);
                    gamePlayer.setActive(true);
                    
                    // Start the round - dealer deals cards
                    house.startNewRound();
                    
                    // Show dealer's face-up card
                    Card dealerFaceUpCard = house.showOneCard();
                    System.out.println("\n=== Current Round ===");
                    System.out.println("Dealer's showing: " + dealerFaceUpCard);
                    System.out.println("Dealer has one card face down.\n");
                    
                    boolean playerTurn = true;
                    Hand currentHand = gamePlayer.getHand();
                    
                    // Player's turn
                    while (playerTurn && currentHand.getValue() <= 21 && !currentHand.isBusted()) {
                        System.out.println("Player's hand:");
                        for (int i = 0; i < currentHand.getCards().size(); i++) {
                            System.out.println("Card " + (i + 1) + ": " + currentHand.getCards().get(i));
                        }
                        System.out.println("Hand value: " + currentHand.getValue());

                        printGameOptions();
                        String playChoice = scanner.nextLine().trim();

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
                                continue;
                        }
                        
                        // Process the player's action through dealer
                        house.processPlayerTurns();
                        
                        // Check if player busted after hit
                        if (currentHand.getValue() > 21) {
                            currentHand.setBusted(true);
                            System.out.println("Player busted with " + currentHand.getValue() + "!");
                            playerTurn = false;
                        }
                    }
                    
                    // Dealer's turn (only if player didn't bust)
                    if (!currentHand.isBusted()) {
                        System.out.println("\nDealer's turn...");
                        house.playTurn();
                    } else {
                        System.out.println("\nSkipping dealer's turn - player busted.");
                    }
                    
                    // Settle bets
                    house.settleBets();
                    
                    // Display final results
                    System.out.println("\n=== Final Results ===");
                    
                    // Show dealer's full hand
                    System.out.println("Dealer's full hand:");
                    List<Card> dealerCards = house.getHandCards();
                    for (int i = 0; i < dealerCards.size(); i++) {
                        System.out.println("Card " + (i + 1) + ": " + dealerCards.get(i));
                    }
                    System.out.println("Dealer's hand value: " + house.getHandValue());
                    
                    // Show player's final hand
                    System.out.println("\nPlayer's final hand:");
                    for (int i = 0; i < currentHand.getCards().size(); i++) {
                        System.out.println("Card " + (i + 1) + ": " + currentHand.getCards().get(i));
                    }
                    System.out.println("Player's hand value: " + currentHand.getValue());

                    // Determine winner based on standard Blackjack rules
                    int dealerValue = house.getHandValue();
                    int playerValue = currentHand.getValue();
                    boolean dealerBusted = dealerValue > 21;
                    
                    if (currentHand.isBusted()) {
                        playerWon = false;
                        System.out.println("\n>>> Player busted! Dealer wins. <<<");
                    } else if (dealerBusted) {
                        playerWon = true;
                        System.out.println("\n>>> Dealer busted! Player wins! <<<");
                    } else if (playerValue > dealerValue) {
                        playerWon = true;
                        System.out.println("\n>>> Player wins with " + playerValue + " against dealer's " + dealerValue + "! <<<");
                    } else if (playerValue == dealerValue) {
                        playerWon = false;
                        System.out.println("\n>>> It's a tie! Push - bet returned. <<<");
                        gamePlayer.addWinnings(betAmount);
                    } else {
                        playerWon = false;
                        System.out.println("\n>>> Dealer wins with " + dealerValue + " against player's " + playerValue + ". <<<");
                    }

                    // Reset dealer's hand but keep players for next round
                    house.resetHand();
                    
                    // Update database with final balance
                    updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance());

                    System.out.println();

                    // Award winnings if player wins
                    if (playerWon) {
                        System.out.println("Congratulations! You won this round!");
                        gamePlayer.addWinnings(betAmount * 2);
                        boolean winningsUpdated = updateBalanceInDB(conn, gamePlayer.getPlayerId(), gamePlayer.getChipBalance());
                        if(!winningsUpdated) {
                            System.out.println("Failed to update balance in database.");
                        }
                        System.out.println("New balance: $" + gamePlayer.getChipBalance());
                    } else if (playerValue == dealerValue && !currentHand.isBusted() && !dealerBusted) {
                        // Push - bet already returned
                        System.out.println("Your bet of $" + betAmount + " has been returned.");
                        System.out.println("Current balance: $" + gamePlayer.getChipBalance());
                    } else {
                        System.out.println("You lost $" + betAmount + " this round.");
                        System.out.println("Current balance: $" + gamePlayer.getChipBalance());
                    }

                    // Ask player if they want to play again
                    System.out.print("\nDo you want to play again? (1 for yes, 2 for no): ");
                    String again = scanner.nextLine().trim();

                    if (again.equals("2")) {
                        System.out.println("Exiting game...");
                        keepPlaying = false;
                    } else if (again.equals("1")) {
                        System.out.println("Starting a new game...");
                        house.removePlayer(gamePlayer.getPlayerId());
                        gamePlayer.resetForNewRound();
                    } else {
                        System.out.println("INVALID CHOICE. Returning to menu.");
                        keepPlaying = false;
                    }
                }
            } else {
                System.out.println("Insufficient balance to place bet. Your balance: $" + 
                    gamePlayer.getChipBalance() + ", Bet attempted: $" + betAmount);
                System.out.println("Please add more money to your account or try a smaller bet.");
                keepPlaying = false;
            }
        }
    }

    /**
     * Configures logging for the application to only show warnings and errors, suppressing info logs.
     */
    private static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        Logger playerLogger = Logger.getLogger(Player.class.getName());
        
        // Remove all existing handlers
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        
        // Create a new console handler with custom formatting
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                // Only show the message, no timestamps or class names
                return record.getMessage() + "\n";
            }
        });
        
        // Set level to only show warnings and errors (suppress INFO logs)
        consoleHandler.setLevel(Level.WARNING);
        playerLogger.addHandler(consoleHandler);
        playerLogger.setLevel(Level.WARNING);
        playerLogger.setUseParentHandlers(false); // Prevent propagation to root logger
    }

    /**
     * Main method that launches the Blackjack program.
     * Connects to the database, authenticates the user, 
     * and displays the main menu options.
     *
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        configureLogging();

        Scanner scanner = new Scanner(System.in);
        System.out.println("=======================================");
        System.out.println("  Welcome to the Blackjack Game!");
        System.out.println("=======================================");

        boolean validAccount = false;
        Connection conn = null;

        String DB_URL = "jdbc:oracle:thin:@10.110.10.90:1521:oracle";
        String USER = "IT326S09";
        String PASS = "pink22";

        String name = "";
        int id = -1;

        // SHALOM - Generate cryptographic keys
        try {
            encryptionKey = EncryptAndDecrypt.generateKey("AES");
            java.security.KeyPairGenerator kpg = java.security.KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            java.security.KeyPair kp = kpg.generateKeyPair();
            signatureKey = kp.getPrivate();
            verificationKey = kp.getPublic();
            System.out.println("Cryptographic keys generated successfully.");
        } catch (Exception e) {
            System.err.println("Key generation error: " + e.getMessage());
        }
        
        try {
            // Establish database connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Database connection established.");

            // Authenticate user
            while (!validAccount) {
                System.out.print("\nPlease enter your name: ");
                name = scanner.nextLine();
                System.out.print("Please enter your ID: ");
                id = scanner.nextInt();
                scanner.nextLine();

                validAccount = updateNameInDB(conn, id, name);

                if (!validAccount) {
                    System.out.println("Invalid name or ID, please try again.");
                    System.out.println();
                }
            }

            double currBalance = getBalanceFromDB(conn, id);
            
            // Handle case when balance is not found (-1.0)
            if (currBalance < 0) {
                System.out.println("\nNo existing balance found. Starting with $100.00.");
                currBalance = 100.00;
                
                String insertSql = "INSERT INTO account_balance (id, name, balance) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, id);
                    insertStmt.setString(2, name);
                    insertStmt.setDouble(3, currBalance);
                    insertStmt.executeUpdate();
                    System.out.println("New account created for " + name + " with ID " + id);
                } catch (SQLException e) {
                    System.err.println("Could not create initial balance record: " + e.getMessage());
                    // If insert fails, try to get the balance again
                    currBalance = getBalanceFromDB(conn, id);
                    if (currBalance < 0) {
                        System.err.println("Fatal: Cannot create or retrieve account. Exiting.");
                        return;
                    }
                }
            }

            Player gamePlayer = new Player(id, name, currBalance);
            Regular regularPlayer = new Regular(name, currBalance);

            String choice = "";
            System.out.println("\nYour current balance is: $" + currBalance);

            // Main menu loop
            while (!choice.equals("4")) {
                System.out.println();
                System.out.println("Hello " + name + ", what would you like to do?");
                System.out.println("----------------------------------------");

                printOptions(regularPlayer);
                choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.println("\n--- PLAY GAME ---");
                        playGame(scanner, gamePlayer, regularPlayer, conn);
                        break;

                    case "2":
                        System.out.println("\n--- ADD MONEY ---");
                        System.out.print("Enter amount to add: $");
                        double amountToAdd = scanner.nextDouble();
                        scanner.nextLine();
                        
                        if (amountToAdd > 0) {
                            double currentBalance = getBalanceFromDB(conn, id);
                            double newBalance = currentBalance + amountToAdd;
                            
                            boolean changedAmount = updateBalanceInDB(conn, id, newBalance);
                            
                            if (changedAmount) {
                                regularPlayer.addBalance(amountToAdd);
                                gamePlayer.addMoney(amountToAdd);
                                System.out.println("Balance updated successfully!");
                                System.out.println("New balance: $" + getBalanceFromDB(conn, id));
                            } else {
                                System.out.println("Failed to update balance.");
                            }
                        } else {
                            System.out.println("Amount must be positive.");
                        }
                        break;

                    case "3":
                        System.out.println("\n--- VIEW BALANCE ---");
                        System.out.println("Your current balance is: $" + gamePlayer.getChipBalance());
                        break;

                    case "4":
                        System.out.println("\n--- EXIT ---");
                        System.out.println("Thank you for playing Blackjack! Goodbye.");
                        break;

                    // SHALOM
                    case "5":
                        System.out.println("\n--- SAVE PLAYER PROFILE ---");
                        if (encryptionKey == null || signatureKey == null) {
                            System.err.println("Encryption or signature key not initialized.");
                        } else {
                            boolean saved = saveFile.savePlayer(gamePlayer, encryptionKey, signatureKey);
                            if (saved) {
                                System.out.println("Player profile saved successfully.");
                            } else {
                                System.out.println("Failed to save player profile.");
                            }
                        }
                        break;
                        
                    case "6":
                        System.out.println("\n--- LOAD PLAYER PROFILE ---");
                        if (encryptionKey == null || verificationKey == null) {
                            System.err.println("Encryption or verification key not initialized.");
                        } else {
                            Player loadedPlayer = saveFile.loadPlayer(encryptionKey, verificationKey);
                            if (loadedPlayer != null) {
                                gamePlayer = loadedPlayer;
                                regularPlayer = new Regular(gamePlayer.getName(), gamePlayer.getChipBalance());
                                System.out.println("Player profile loaded successfully.");
                                System.out.println("Welcome back, " + gamePlayer.getName() + "!");
                                System.out.println("Balance: $" + gamePlayer.getChipBalance());
                            } else {
                                System.out.println("Failed to load player profile.");
                            }
                        }
                        break;
                        
                    case "7":
                        System.out.println("\n--- VIP PROMOTION ---");
                        if(regularPlayer.isEligibleForVIP()){
                            Date lastLogin = new Date();
                            VIP vipPlayer = new VIP(regularPlayer.getPlayerName(), 
                                                   regularPlayer.getBalance(), 
                                                   regularPlayer.getGamesPlayed(), 
                                                   lastLogin); 
                            System.out.println("Congratulations! You are eligible for our VIP promotion!");
                            printVIPOptions();
                            String vipChoice = scanner.nextLine();

                            if(vipChoice.equals("1")){
                                System.out.print("Select a perk from the available options: " + 
                                    vipPlayer.getAvailablePerks() + ": ");
                                String perkChoice = scanner.nextLine();
                                vipPlayer.selectPerk(perkChoice);
                            } else if(vipChoice.equals("2")){
                                String os = System.getProperty("os.name").toLowerCase();
                                boolean isWindows = os.contains("win");

                                System.out.println("\nChoose a directory listing option:");
                                System.out.println("1. Basic listing");
                                System.out.println("2. Show hidden files");
                                System.out.println("3. Detailed listing");
                                System.out.print("Enter your choice: ");

                                int choiceCommand = scanner.nextInt();
                                scanner.nextLine(); // consume newline
                                
                                String[] command = buildCommand(choiceCommand, isWindows);
                                
                                if (command != null) {
                                    try {
                                        Process process = Runtime.getRuntime().exec(command);
                                        int exitCode = process.waitFor();

                                        if (exitCode != 0) {
                                            System.out.println("Command failed with exit code " + exitCode);
                                        } else {
                                            System.out.println("\nCommand output:");
                                            process.getInputStream().transferTo(System.out);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Error executing command: " + e.getMessage());
                                    }
                                } else {
                                    System.out.println("Invalid command choice.");
                                }
                            } else {
                                System.out.println("INVALID CHOICE.");
                            }
                        } else {
                            int gamesNeeded = 10 - regularPlayer.getGamesPlayed();
                            System.out.println("Sorry, you are not eligible for the VIP promotion yet.");
                            System.out.println("You need " + gamesNeeded + " more game(s) to unlock VIP status.");
                        }
                        break;    
                        
                    default:
                        System.out.println("INVALID CHOICE. Please enter a number between 1 and " + 
                            (regularPlayer.isEligibleForVIP() ? "7" : "6") + ".");
                }
                System.out.println();
            }

            scanner.close();

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            System.err.format("SQL State: %s\n", e.getSQLState());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close database connection
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing database connection: " + e.getMessage());
                }
            }
        }
    }
}