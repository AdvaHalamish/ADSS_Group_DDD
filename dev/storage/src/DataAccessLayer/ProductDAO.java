package DataAccessLayer;

import BuisnessLayer.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDAO {
    protected static Connection conn;
    Map<String, Product> products = new HashMap<>();
    public static ProductDAO instance = null;

    public ProductDAO(Connection connection) {
        conn = Database.getConnection();
    }

    public static ProductDAO getInstance() {
        if (instance == null) {
            instance = new ProductDAO(Database.getConnection());
        }
        return instance;
    }

    // Method to insert a new product into the database with associated items
    public static boolean insertProduct(Product product) {
        try {
            // Begin transaction
            conn.setAutoCommit(false);

            // Insert product
            String productSql = "INSERT INTO products (productCode, productName, category, subCategory, size, manufacturer, costPrice, sellingPrice, status, quantityInStore, quantityInWarehouse, minimumQuantityForAlert) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            setProductStatement(productStmt, product);
            productStmt.executeUpdate();

            // Insert associated items
            insertItems(product.getItems(), product.getProductCode());

            // Commit transaction
            conn.commit();
            //System.out.println("Product inserted successfully: " + product.getProductCode());
            return true;
        } catch (SQLException e) {
            handleSQLException(e, "insertProduct");
        } finally {
            resetAutoCommit();
        }
        return false;
    }

    // Method to update an existing product in the database
    public void updateProduct(Product product) {
        try {
            String sql = "UPDATE products SET productName = ?, category = ?, subCategory = ?, size = ?, manufacturer = ?, " +
                    "costPrice = ?, sellingPrice = ?, status = ?, quantityInStore = ?, quantityInWarehouse = ?, minimumQuantityForAlert = ? WHERE productCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            setProductStatementupdate(stmt, product);
            stmt.setString(12, product.getProductCode());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }
    private static void setProductStatementupdate(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getProductName());
        stmt.setString(2, product.getCategory());
        stmt.setString(3, product.getSubCategory());
        stmt.setDouble(4, product.getSize());
        stmt.setString(5, product.getManufacturer());
        stmt.setDouble(6, product.getPurchasePrice());
        stmt.setDouble(7, product.getSellingPrice());
        stmt.setString(8, product.getStatus().name());
        stmt.setInt(9, product.getQuantityInStore());
        stmt.setInt(10, product.getQuantityInWarehouse());
        stmt.setInt(11, product.getMinimumQuantityForAlert());
    }

    // Method to delete a product from the database
    public void deleteProduct(String productCode) {
        try {
            String sql = "DELETE FROM products WHERE productCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productCode);
            stmt.executeUpdate();
            deleteItemsByProductCode(productCode);

        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    private void deleteItemsByProductCode(String productCode) {
        String sql = "DELETE FROM items WHERE productCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to get products by category
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT * FROM products WHERE category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getProductsByCategory: " + e.getMessage());
        }
        return products;
    }

    // Method to insert an item associated with a product into the database
    public boolean insertItem(Item item, String productCode) throws SQLException {
        return insertItems(Map.of(item.getItemCode(), item), productCode);
    }

    // Method to fetch all products from the database
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT * FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all products: " + e.getMessage());
        }
        return products;
    }

    // Method to close connection
    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public Product getProductByName(String nameProduct) {
        Product product = null;
        try {
            String sql = "SELECT * FROM products WHERE productName = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nameProduct);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = createProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getProductByName: " + e.getMessage());
        }
        return product;
    }

    public Product getProductByCode(String codeProduct) {
        Product product = null;
        try {
            String sql = "SELECT * FROM products WHERE productCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, codeProduct);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                product = createProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getProductByCode: " + e.getMessage());
        }
        return product;
    }

    public int getTotalQuantityInStore() throws SQLException {
        return getTotalQuantity("quantityInStore");
    }

    public int getTotalQuantityInWarehouse() throws SQLException {
        return getTotalQuantity("quantityInWarehouse");
    }

    public List<Product> getProductsWithLowQuantity() {
        List<Product> products = new ArrayList<>();
        try {
            String sql = "SELECT * FROM products WHERE quantityInWarehouse + quantityInStore < minimumQuantityForAlert";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                products.add(product);
            }
        } catch (SQLException e) {
            System.out.println("Error in getProductsWithLowQuantity: " + e.getMessage());
        }
        return products;
    }

    // Helper method to create a Product object from a ResultSet
    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        String productCode = rs.getString("productCode");
        String productName = rs.getString("productName");
        String category = rs.getString("category");
        String subCategory = rs.getString("subCategory");
        double size = rs.getDouble("size");
        String manufacturer = rs.getString("manufacturer");
        double costPrice = rs.getDouble("costPrice");
        double sellingPrice = rs.getDouble("sellingPrice");
        String statusString = rs.getString("status");
        ProductStatus status = ProductStatus.valueOf(statusString);
        int quantityInStore = rs.getInt("quantityInStore");
        int quantityInWarehouse = rs.getInt("quantityInWarehouse");
        int minimumQuantityForAlert = rs.getInt("minimumQuantityForAlert");

        HashMap<String, Item> items = fetchItemsByProductCode(productCode);
        Discount discount = DiscountDAO.getInstance().getDiscountByProductCode(productCode);
        return new Product(quantityInStore, quantityInWarehouse, minimumQuantityForAlert, costPrice, manufacturer, sellingPrice, productName, category, subCategory, size, productCode, status, discount, items);
    }

    // Helper method to fetch items by product code
    private HashMap<String, Item> fetchItemsByProductCode(String productCode) throws SQLException {
        HashMap<String, Item> items = new HashMap<>();
        String itemSql = "SELECT * FROM items WHERE productCode = ?";
        PreparedStatement itemStmt = conn.prepareStatement(itemSql);
        itemStmt.setString(1, productCode);
        ResultSet itemRs = itemStmt.executeQuery();
        while (itemRs.next()) {
            String itemCode = itemRs.getString("itemCode");
            String stored = itemRs.getString("stored");
            LocalDate expirationDate = LocalDate.parse(itemRs.getString("expirationDate"));
            String itemStatus = itemRs.getString("status");
            Item item = new Item(ItemPlace.valueOf(stored), itemCode, expirationDate, ItemStatus.valueOf(itemStatus));
            items.put(itemCode, item);
        }
        return items;
    }

    // Helper method to insert items
    private static boolean insertItems(Map<String, Item> items, String productCode) throws SQLException {
        String itemSql = "INSERT INTO items (itemCode, productCode, stored, expirationDate, status) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement itemStmt = conn.prepareStatement(itemSql);
        for (Item item : items.values()) {
            itemStmt.setString(1, item.getItemCode());
            itemStmt.setString(2, productCode);
            itemStmt.setString(3, item.getStored().name());
            itemStmt.setString(4, item.getExpirationDate().toString());
            itemStmt.setString(5, item.getStatus().name());
            itemStmt.executeUpdate();
        }
        return true;
    }

    // Helper method to set product statement parameters
    private static void setProductStatement(PreparedStatement stmt, Product product) throws SQLException {
        stmt.setString(1, product.getProductCode());
        stmt.setString(2, product.getProductName());
        stmt.setString(3, product.getCategory());
        stmt.setString(4, product.getSubCategory());
        stmt.setDouble(5, product.getSize());
        stmt.setString(6, product.getManufacturer());
        stmt.setDouble(7, product.getPurchasePrice());
        stmt.setDouble(8, product.getSellingPrice());
        stmt.setString(9, product.getStatus().name());
        stmt.setInt(10, product.getQuantityInStore());
        stmt.setInt(11, product.getQuantityInWarehouse());
        stmt.setInt(12, product.getMinimumQuantityForAlert());
    }

    // Helper method to handle SQL exceptions and rollback transactions
    private static void handleSQLException(SQLException e, String methodName) {
        System.out.println("SQLException occurred in " + methodName + ": " + e.getMessage());
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException occurred during rollback: " + ex.getMessage());
        }
    }

    // Helper method to reset auto-commit mode
    private static void resetAutoCommit() {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Error resetting auto-commit: " + e.getMessage());
        }
    }

    // Helper method to get total quantity by column
    private int getTotalQuantity(String columnName) throws SQLException {
        int totalQuantity = 0;
        String sql = "SELECT SUM(" + columnName + ") AS totalQuantity FROM products";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalQuantity = rs.getInt("totalQuantity");
            }
        } catch (SQLException e) {
            System.out.println("Error in getTotalQuantity: " + e.getMessage());
        }
        return totalQuantity;
    }

    public boolean updateProductStatus(String code, ProductStatus newStatus) throws SQLException {
        String sql = "UPDATE products SET status = ? WHERE productCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus.toString());
            stmt.setString(2, code);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }
}
