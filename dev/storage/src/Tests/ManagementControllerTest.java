package Tests;

import BuisnessLayer.*;
import DataAccessLayer.ProductDAO;
import DataAccessLayer.ItemDAO;
import DataAccessLayer.DiscountDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ManagementControllerTest {
    private ManagementController managementController;
    private ProductDAO productDAO;
    private ItemDAO itemDAO;
    private DiscountDAO discountDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        managementController = new ManagementController(true);
        productDAO = ProductDAO.getInstance();
        itemDAO = ItemDAO.getInstance();
        discountDAO = DiscountDAO.getInstance();
    }

    @Test
    public void testInsertProduct() throws SQLException {
        Product product = new Product("Test Inc.", "Electronics", "Test Product", "Gadgets", "T001", 1.0, 1.0, 1.0, 100, 10, ItemPlace.Store, LocalDate.now().plusDays(10));
        productDAO.insertProduct(product);

        Product retrievedProduct = productDAO.getProductByCode("T001");
        assertNotNull(retrievedProduct);
        assertEquals("Test Product", retrievedProduct.getProductName());
    }

    @Test
    public void testDeleteProduct() throws SQLException {
        Product product = new Product("Test Inc.", "Electronics", "Test Product", "Gadgets", "T001", 1.0, 1.0 ,1.0, 100, 10, ItemPlace.Store, LocalDate.now().plusDays(10));
        productDAO.insertProduct(product);

        productDAO.deleteProduct("T001");
        Product retrievedProduct = productDAO.getProductByCode("T001");
        assertNull(retrievedProduct);
    }

    @Test
    public void testInsertAndDeleteAllProducts() throws SQLException {
        managementController.insertDetails();
        List<Product> products = productDAO.getAllProducts();
        assertEquals(2, products.size());

        managementController.deleteAllProductsAndItemsAndDiscounts();
        products = productDAO.getAllProducts();
        assertEquals(0, products.size());
    }

    @Test
    public void testInsertAndRetrieveProducts() throws SQLException {
        managementController.insertDetails();
        List<Product> products = productDAO.getAllProducts();
        assertEquals(2, products.size());

        Product product = productDAO.getProductByCode("A001");
        assertNotNull(product);
        assertEquals("Apple", product.getProductName());
    }

    @Test
    public void testUpdateProductStatus() throws SQLException {
        Product product = new Product("Test Inc.", "Electronics", "Test Product", "Gadgets", "T001", 1.0, 1.0, 1.0, 100, 10, ItemPlace.Store, LocalDate.now().plusDays(10));
        productDAO.insertProduct(product);

        productDAO.updateProductStatus("T001", ProductStatus.NotInStorage);
        Product retrievedProduct = productDAO.getProductByCode("T001");
        assertNotNull(retrievedProduct);
        assertEquals(ProductStatus.NotInStorage, retrievedProduct.getStatus());
    }

    @Test
    public void testInsertDiscount() throws SQLException {
        managementController.insertDetails();
        Discount discount = discountDAO.getDiscountByProductCode("A001");
        assertNotNull(discount);
        assertEquals(0.1, discount.getDiscountRate());
    }

    @Test
    public void testRetrieveDiscounts() throws SQLException {
        managementController.insertDetails();
        Discount discount = discountDAO.getDiscountByProductCode("B002");
        assertNotNull(discount);
        assertEquals(0.15, discount.getDiscountRate());
    }

    @Test
    public void testApplyDiscountToProduct() throws SQLException {
        discountDAO.deleteDiscount("M003");
        productDAO.deleteProduct("M003");
        itemDAO.deleteItemsByProductCode("M003");
        Product product = new Product("Dairy Farms", "Dairy", "Milk", "Milk", "M003", 4.0, 4.0, 1.0, 8, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
        productDAO.insertProduct(product);
        managementController.applyDiscount("M003", 0.2, LocalDate.parse("2024-07-01"), LocalDate.parse("2024-08-10"));
        Product updatedProduct = productDAO.getProductByCode("M003");
        assertEquals(0.8 * updatedProduct.getPurchasePrice(), updatedProduct.getSellingPrice());

    }

    @Test
    public void testInsertAndRetrieveItems() throws SQLException {
        managementController.insertDetails();
        List<Item> items = itemDAO.getItemsByProductCode("A001");
        assertEquals(1, items.size());
    }

    @Test
    public void testDeleteItemsByProductCode() throws SQLException {
        managementController.insertDetails();
        itemDAO.deleteItemsByProductCode("A001");
        List<Item> items = itemDAO.getItemsByProductCode("A001");
        assertEquals(0, items.size());
    }
    @Test
    public void testUpdateItemStatus() throws SQLException {
        String itemCode = "M003-0";
        String productCode = "M003";
        ItemStatus newStatus = ItemStatus.Sold;
        ItemDAO.insert(new Item(ItemPlace.Store, itemCode, LocalDate.now().plusDays(10),ItemStatus.Available),productCode);
        boolean updateResult = itemDAO.update(itemCode, newStatus);
        // Assert - check if the update was successful
        assertTrue(updateResult, "The item status should be updated successfully.");
        // Retrieve the updated item and check its status
        Item updatedItem = itemDAO.getItemByCode(itemCode);
        assertEquals(newStatus, updatedItem.getStatus(), "The item status should be updated to Sold.");
    }
}
