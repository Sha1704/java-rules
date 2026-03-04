import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;         
import java.sql.SQLException;      

public class OracleHandler {
    public static void main(String[] args) throws SQLException {

        Connection conn = null; // Holds the DB connection
        String DB_URL = "jdbc:oracle:thin:@10.110.10.90:1521:oracle";
        String USER = "IT326S09";
        String PASS = "pink22";

        try {
            // Connect to Oracle
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            if (conn != null) {
                System.out.println("Connected to the Oracle DB!");
            } else {
                System.out.println("Failed to make connection!");
            }

            // Query to get balance by ID
            String sql = "SELECT balance FROM account_balance WHERE id = ?";

            try (PreparedStatement state = conn.prepareStatement(sql)) {
                state.setInt(1, 1); // Set ID parameter

                try (ResultSet rs = state.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Balance: " + rs.getDouble("balance"));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close connection if open
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