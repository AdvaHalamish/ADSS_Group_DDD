package Tests;

import java.sql.SQLException;
import java.time.LocalDate;
import BuisnessLayer.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {
    private Product product;
    private Item item1;

    @BeforeEach
    public void setUp() throws SQLException {
        product = new Product("Manufacturer", "Category", "ProductName", "SubCategory", "100", 10.0,10.0, 1.0, 10, 5,  ItemPlace.Store, LocalDate.now().plusDays(10));
    }

    @Test
    public void testAddItem() throws SQLException {
        product.addItems(1, ItemPlace.Warehouse, LocalDate.now().plusDays(10), ItemStatus.Available);
        assertEquals(11, product.getItems().size());
    }

    @Test
    public void testRemoveItem() throws SQLException {
        product.removeItem("100-1", ItemStatus.Sold);
        assertEquals(10, product.getItems().size());
        item1=product.getItem("100-1");
        assertEquals(ItemStatus.Sold, item1.getStatus());
    }

    @Test
    public void testApplyDiscount() {
        Discount discount = new Discount(0.1, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        product.applyDiscount(discount);
        assertEquals(9.0, product.getSellingPrice());
    }

    @Test
    public void testGetCategory() {
        assertEquals("Category", product.getCategory());
    }

    @Test
    public void testGetSubCategory() {
        assertEquals("SubCategory", product.getSubCategory());
    }

    @Test
    public void testGetSize() {
        assertEquals(1.0, product.getSize());
    }

    @Test
    public void testGetProductName() {
        assertEquals("ProductName", product.getProductName());
    }

    @Test
    public void testGetQuantityInStore() {
        assertEquals(10, product.getQuantityInStore());
    }

    @Test
    public void testGetQuantityInWarehouse() throws SQLException {
        product.addItems(1, ItemPlace.Warehouse, LocalDate.now().plusDays(10), ItemStatus.Available);
        assertEquals(1, product.getQuantityInWarehouse());
    }

    @Test
    public void testGetManufacturer() {
        assertEquals("Manufacturer", product.getManufacturer());
    }

}
