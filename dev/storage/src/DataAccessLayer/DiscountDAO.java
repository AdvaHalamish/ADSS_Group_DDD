package DataAccessLayer;

import BuisnessLayer.Discount;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static DataAccessLayer.ProductDAO.conn;

public class DiscountDAO {
    private static DiscountDAO instance;
    private static Connection connection;

    public DiscountDAO(Connection connection) {
        this.connection = Database.getConnection();
    }

    public static DiscountDAO getInstance() {
        if (instance == null) {
            instance = new DiscountDAO(Database.getConnection());
        }
        return instance;
    }

    public static boolean insertDiscount(String productCode, Discount discount) {
        String sql = "INSERT INTO discounts (discountRate, startDate, endDate,productCode) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setDouble(1, discount.getDiscountRate());
            pstmt.setString(2, discount.getStartDate().toString());
            pstmt.setString(3, discount.getEndDate().toString());
            pstmt.setString(4, productCode);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Insert discount failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateDiscount(String productCode, Discount discount) {
        String sql = "UPDATE discounts SET discountRate = ?, startDate = ?, endDate = ? WHERE productCode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, discount.getDiscountRate());
            pstmt.setString(2, discount.getStartDate().toString());
            pstmt.setString(3, discount.getEndDate().toString());
            pstmt.setString(4, productCode);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Update discount failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDiscount(String productCode) {
        String sql = "DELETE FROM discounts WHERE productCode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Delete discount failed: " + e.getMessage());
            return false;
        }
    }

    public Discount getDiscountByProductCode(String productCode) {
        String sql = "SELECT * FROM discounts WHERE productCode = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, productCode);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double discountPercentage = rs.getDouble("discountRate");
                LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
                LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
                return new Discount(discountPercentage, startDate, endDate);
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Get discount by product code failed: " + e.getMessage());
            return null;
        }
    }

    public List<Discount> getAllDiscounts() {
        List<Discount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM discounts";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                double discountPercentage = rs.getDouble("discountRate");
                LocalDate startDate = LocalDate.parse(rs.getString("startDate"));
                LocalDate endDate = LocalDate.parse(rs.getString("endDate"));
                discounts.add(new Discount(discountPercentage, startDate, endDate));
            }
        } catch (SQLException e) {
            System.out.println("Get all discounts failed: " + e.getMessage());
        }
        return discounts;
    }

    public void deleteDiscountsByProductCode(String productCode) throws SQLException {
        String sql = "DELETE FROM discounts WHERE productCode = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, productCode);
            stmt.executeUpdate();
        }
    }
}
