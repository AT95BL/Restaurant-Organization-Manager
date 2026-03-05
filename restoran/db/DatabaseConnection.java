package restoran.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/restoran?useSSL=false&serverTimezone=Europe/Sarajevo&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8";
    // private static final String URL      = "jdbc:mysql://localhost:3306/restoran?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "StrongPassword123!";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("✔ Konekcija sa bazom uspostavljena.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver nije pronađen!", e);
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri konekciji sa bazom: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Greška pri provjeri konekcije: " + e.getMessage(), e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✔ Konekcija zatvorena.");
            }
        } catch (SQLException e) {
            System.err.println("Greška pri zatvaranju konekcije: " + e.getMessage());
        }
    }
}
