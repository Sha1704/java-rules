import java.sql.Connection;        
import java.sql.DriverManager;     
import java.sql.PreparedStatement; 
import java.sql.ResultSet;         
import java.sql.SQLException;      

public class OracleHandler {
    private Connection conn;
    public OracleHandler() {

        Connection conn = null; // Holds the DB connection
        String DB_URL = "jdbc:oracle:thin:@10.110.10.90:1521:oracle";
        String USER = "IT326S09";
        String PASS = "pink22";

        try {
            // Connect to Oracle
            
            conn = DriverManager.getConnection(DB_URL, USER, PASS);


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