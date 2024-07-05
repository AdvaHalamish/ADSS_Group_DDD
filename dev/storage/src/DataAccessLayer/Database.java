package DataAccessLayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:release/mydatabase.db";
    private static Database instance;
    private static Connection connection;


    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found.", e);
            }
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON;");


            String createProductsTable = "CREATE TABLE IF NOT EXISTS products (\n"
                    + " productCode TEXT PRIMARY KEY,\n"
                    + " productName TEXT NOT NULL,\n"
                    + " category TEXT NOT NULL,\n"
                    + " subCategory TEXT NOT NULL,\n"
                    + " size REAL NOT NULL,\n"
                    + " manufacturer TEXT NOT NULL,\n"
                    + " costPrice REAL NOT NULL,\n"
                    + " sellingPrice REAL NOT NULL,\n"
                    + " status TEXT NOT NULL,\n"
                    + " quantityInStore INTEGER NOT NULL,\n"
                    + " quantityInWarehouse INTEGER NOT NULL,\n"
                    + " minimumQuantityForAlert INTEGER NOT NULL\n"
                    + ");";

            String createItemsTable = "CREATE TABLE IF NOT EXISTS items (\n"
                    + " itemCode TEXT PRIMARY KEY,\n"
                    + " productCode TEXT NOT NULL,\n"
                    + " stored TEXT NOT NULL,\n"
                    + " expirationDate TEXT NOT NULL,\n"
                    + " status TEXT NOT NULL,\n"
                    + " FOREIGN KEY (productCode) REFERENCES products (productCode)\n"
                    + ");";

            String createDiscountsTable = "CREATE TABLE IF NOT EXISTS discounts (\n"
                    + " productCode TEXT NOT NULL,\n"
                    + " discountPercentage REAL NOT NULL,\n"
                    + " startDate TEXT NOT NULL,\n"
                    + " endDate TEXT NOT NULL,\n"
                    + " FOREIGN KEY (productCode) REFERENCES products (productCode)\n"
                    + ");";

            stmt.execute(createProductsTable);
            stmt.execute(createItemsTable);
            stmt.execute(createDiscountsTable);

            //System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database");
        }
        return connection;
    }
    // Close database connection
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
        }
    }
}
