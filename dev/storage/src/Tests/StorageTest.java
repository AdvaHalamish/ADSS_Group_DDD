//package Tests;
//
//import BuisnessLayer.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class StorageTest {
//    private Storage storage;
//
//    @BeforeEach
//    public void setUp() {
//        storage = new Storage();
//    }
//
//    @Test
//    public void testInsertProduct() throws SQLException {
//        Product product = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 1.0, 10.0, 1, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//        storage.insertProduct(product);
//        assertEquals(1, storage.getAllProducts().size());
//    }
//
//    @Test
//    public void testInserterProduct() throws SQLException {
//        Product product = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 1.0, 10.0, 1, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//        storage.insertProduct(product);
//        storage.insertItemstoProduct(product, 2, ItemPlace.Store, LocalDate.now().plusDays(10), ItemStatus.Available);
//        assertEquals(3, storage.getAllProducts().getFirst().getTotalQuantity());
//    }
//    @Test
//    public void testGetProductByName() throws SQLException {
//        Product product1 = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 1.0, 10.0, 1, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//        Product product2 = new Product("Manufacturer2", "Category2", "Product2", "SubCategory2", "Code2", 2.0, 20.0, 1, 5, ItemPlace.Warehouse, LocalDate.now().plusDays(10));
//
//        storage.insertProduct(product1);
//        storage.insertProduct(product2);
//
//        Product retrievedProduct = storage.getProductByName("Product2");
//        assertNotNull(retrievedProduct);
//        assertEquals("Product2", retrievedProduct.getProductName());
//    }
//
//    @Test
//    public void testApplyDiscountToCategory() throws SQLException {
//        Product product1 = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 10.0, 10.0, 1, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//        Product product2 = new Product("Manufacturer2", "Category1", "Product2", "SubCategory2", "Code2", 20.0, 20.0, 1, 5, ItemPlace.Warehouse, LocalDate.now().plusDays(10));
//
//        storage.insertProduct(product1);
//        storage.insertProduct(product2);
//
//        Discount categoryDiscount = new Discount(0.1, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
//        storage.applyDiscountToCategory("Category1", categoryDiscount);
//
//        assertEquals(9.0, product1.getSellingPrice()); // Discount applied to product1
//        assertEquals(18.0, product2.getSellingPrice()); // Discount applied to product2
//    }
//
//    @Test
//    public void testApplyDiscountToProduct() throws SQLException {
//        Product product1 = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 10.0, 10.0, 1, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//
//        storage.insertProduct(product1);
//
//        Discount productDiscount = new Discount(0.2, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
//        storage.applyDiscountToProduct("Product1", productDiscount);
//
//        assertEquals(8.0, product1.getSellingPrice()); // Discount applied to product1
//    }
//
//    @Test
//    public void testGenerateBelowMinimumReport() throws SQLException {
//        Product product1 = new Product("Manufacturer1", "Category1", "Product1", "SubCategory1", "Code1", 1.0, 10.0, 3, 5, ItemPlace.Store, LocalDate.now().plusDays(10));
//        Product product2 = new Product("Manufacturer2", "Category2", "Product2", "SubCategory2", "Code2", 2.0, 20.0, 2, 5, ItemPlace.Warehouse, LocalDate.now().plusDays(10));
//
//        storage.insertProduct(product1);
//        storage.insertProduct(product2);
//
//        // Modify quantity to be below the minimum for both products
//        product1.addItems(-2, ItemPlace.Store, LocalDate.now().plusDays(10), ItemStatus.Available);
//        product2.addItems(-1, ItemPlace.Warehouse, LocalDate.now().plusDays(10), ItemStatus.Available);
//
//        List<Product> belowMinimumReport = storage.generateBelowMinimumReport();
//        assertEquals(2, belowMinimumReport.size()); // Both products should be in the report
//    }
//}
