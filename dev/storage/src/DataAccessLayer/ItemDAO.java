package DataAccessLayer;

import BuisnessLayer.Item;
import BuisnessLayer.ItemPlace;
import BuisnessLayer.ItemStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    private static ItemDAO instance = null;
    private static Connection conn;

    public ItemDAO(Connection connection) {
        conn = Database.getConnection();
    }

    public static ItemDAO getInstance() {
        if (instance == null) {
            instance = new ItemDAO(Database.getConnection());
        }
        return instance;
    }

    public static boolean insert(Item item, String productCode) {
        try {
            String sql = "INSERT INTO items (itemCode, stored, expirationDate, status, productCode) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, item.getItemCode());
            stmt.setString(2, item.getItemPlace().toString());
            stmt.setString(4, item.getExpirationDate().toString());
            stmt.setString(4, item.getItemStatus().toString());
            stmt.setString(5, productCode);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Exception occurred in insert ItemDAO: " + e.getMessage());
            return false;
        }
    }

    public Item getItemByCode(String itemCode) {
        try {
            String sql = "SELECT * FROM items WHERE itemCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ItemPlace itemPlace = ItemPlace.valueOf(rs.getString("stored"));
                LocalDate expirationDate = LocalDate.parse(rs.getString("expirationDate"));
                ItemStatus itemStatus = ItemStatus.valueOf(rs.getString("status"));
                return new Item(itemPlace, itemCode, expirationDate, itemStatus);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getItemByCode ItemDAO: " + e.getMessage());
        }
        return null;
    }

    public boolean update(String itemCode, ItemStatus status) {
        try {
            String sql = "UPDATE items SET status = ? WHERE itemCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.toString());
            stmt.setString(2, itemCode);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Exception occurred in update ItemDAO: " + e.getMessage());
            return false;
        }
    }

    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        try {
            String sql = "SELECT * FROM items";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ItemPlace itemPlace = ItemPlace.valueOf(rs.getString("stored"));
                LocalDate expirationDate = LocalDate.parse(rs.getString("expirationDate")); // Parse directly as LocalDate
                ItemStatus itemStatus = ItemStatus.valueOf(rs.getString("status"));
                String itemCode = rs.getString("itemCode");

                Item item = new Item(itemPlace, itemCode, expirationDate, itemStatus);
                allItems.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getAllItems ItemDAO: " + e.getMessage());
        }
        return allItems;
    }

    public static boolean delete(String itemCode) {
        try {
            String sql = "DELETE FROM items WHERE itemCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemCode);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Exception occurred in delete ItemDAO: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStoredPlace(String itemCode, ItemPlace newPlace) {
        try {
            String sql = "UPDATE items SET stored = ? WHERE itemCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPlace.toString());
            stmt.setString(2, itemCode);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Exception occurred in updateStoredPlace ItemDAO: " + e.getMessage());
            return false;
        }
    }

    public void updateExpiredProductsStatus() {
        try {
            String sql = "UPDATE items SET status = ? WHERE expirationDate < ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ItemStatus.Expired.toString());
            String currentDateStr = LocalDate.now().toString();
            stmt.setString(2, currentDateStr);

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Exception occurred in updateExpiredProductsStatus ItemDAO: " + e.getMessage());
        }
    }

    public List<Item> getItemsByStatus(ItemStatus status) {
        List<Item> items = new ArrayList<>();
        try {
            String sql = "SELECT * FROM items WHERE status = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemCode = rs.getString("itemCode");
                ItemPlace stored = ItemPlace.valueOf(rs.getString("stored"));
                LocalDate expirationDate = LocalDate.parse(rs.getString("expirationDate"));
                ItemStatus itemStatus = ItemStatus.valueOf(rs.getString("status"));

                Item item = new Item(stored, itemCode, expirationDate, itemStatus);
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getItemsByStatus ItemDAO: " + e.getMessage());
        }
        return items;
    }

    public List<Item> getItemsByPlace(ItemPlace place) {
        List<Item> items = new ArrayList<>();
        try {
            String sql = "SELECT * FROM items WHERE stored = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, place.toString());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemCode = rs.getString("itemCode");
                ItemPlace stored = ItemPlace.valueOf(rs.getString("stored"));
                LocalDate expirationDate = LocalDate.parse(rs.getString("expirationDate"));
                ItemStatus itemStatus = ItemStatus.valueOf(rs.getString("status"));

                Item item = new Item(stored, itemCode, expirationDate, itemStatus);
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getItemsByPlace ItemDAO: " + e.getMessage());
        }
        return items;
    }

    public boolean setStatus(String itemCode, ItemStatus status) {
        return update(itemCode, status);
    }


    public void close() {
        // Close connection
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred while closing connection in ItemDAO: " + e.getMessage());
        }
    }

    public List<Item> getItemsByProductCode(String productCode) {
        List<Item> items = new ArrayList<>();
        try {
            String sql = "SELECT * FROM items WHERE productCode = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productCode);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String itemCode = rs.getString("itemCode");
                ItemPlace stored = ItemPlace.valueOf(rs.getString("stored"));
                LocalDate expirationDate = LocalDate.parse(rs.getString("expirationDate"));
                ItemStatus itemStatus = ItemStatus.valueOf(rs.getString("status"));

                Item item = new Item(stored, itemCode, expirationDate, itemStatus);
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Exception occurred in getItemsByProductCode ItemDAO: " + e.getMessage());
        }
        return items;
    }

    public void deleteItemsByProductCode(String productCode) throws SQLException {
        String sql = "DELETE FROM items WHERE productCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            stmt.executeUpdate();
        }
    }
}
