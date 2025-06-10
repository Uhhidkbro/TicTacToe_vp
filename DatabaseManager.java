package tictactoe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    // ✅ Update these values with your MySQL credentials
    private static final String URL = "jdbc:mysql://localhost:3306/tictactoe_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root"; // Replace with your actual username
    private static final String PASSWORD = ""; // Replace with your actual password

    // ✅ Optional: Load driver explicitly (for older versions)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    // 🔌 Get database connection
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("[Database Connection Error]");
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
            e.printStackTrace();
            return null;
        }
    }

    // 💾 Save match result
    public static void saveGameResult(String mode, int boardSize, String winner) {
        String sql = "INSERT INTO game_results (mode, board_size, winner) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            if (conn == null) {
                System.err.println("❌ Cannot save result. No database connection.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, mode);
                stmt.setInt(2, boardSize);
                stmt.setString(3, winner);
                stmt.executeUpdate();
                System.out.println("✅ Game result saved successfully.");
            }

        } catch (SQLException e) {
            System.err.println("[SQL Execution Error]");
            e.printStackTrace();
        }
    }

    // 🧪 Use this to test your DB connection directly
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connected to the database!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("❌ Connection failed.");
        }
    }
}
