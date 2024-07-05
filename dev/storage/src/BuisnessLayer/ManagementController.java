package BuisnessLayer;

import DataAccessLayer.Database;
import DataAccessLayer.ProductDAO;
import DataAccessLayer.ItemDAO;
import DataAccessLayer.DiscountDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ManagementController {
    private ProductDAO productDAO;
    private ItemDAO itemDAO;
    private DiscountDAO discountDAO;

    public ManagementController(boolean usedatabase) throws SQLException {
        productDAO = ProductDAO.getInstance();
        itemDAO = ItemDAO.getInstance();
        discountDAO = DiscountDAO.getInstance();
        if (usedatabase) {
            deleteAllProductsAndItemsAndDiscounts();
            insertDetails();
        }
        else {
            deleteAllProductsAndItemsAndDiscounts();
        }
    }
    public void insertDetails() {
        try {
            // Insert products
            ProductDAO.insertProduct(new Product("Apple Inc.", "Fruits", "Apple", "Fresh", "A001", 2.0,2.0, 1.0, 1, 8, ItemPlace.Store, LocalDate.now().plusDays(10)));
            ProductDAO.insertProduct(new Product("Fruit Co.", "Fruits", "Banana", "Fresh", "B002", 3.0,3.0, 1.0, 5, 10, ItemPlace.Warehouse, LocalDate.now().plusDays(10)));
            ProductDAO.insertProduct(new Product("Dairy Farms", "Dairy", "Milk", "Milk", "M003", 4.0,4.0, 1.0, 8, 5, ItemPlace.Store, LocalDate.now().plusDays(10)));
            ProductDAO.insertProduct(new Product("Dairy Farms", "Dairy", "Cheese", "Cheese", "C004", 5.0, 5.0, 1.0, 12, 6, ItemPlace.Store, LocalDate.now().plusDays(10)));
            ProductDAO.insertProduct(new Product("Dairy Farms", "Dairy", "Yogurt", "Yogurt", "Y005", 3.0, 3.0, 1.0, 5, 5, ItemPlace.Store, LocalDate.now().plusDays(10)));
            ProductDAO.insertProduct(new Product("Bakery Co.", "Bakery", "Bread", "Bread", "B006", 2.0, 2.0, 1.0, 8, 10, ItemPlace.Store, LocalDate.now().plusDays(10)));

            // Insert discounts
            discountDAO.insertDiscount( "A001",new Discount(0.1, LocalDate.parse("2024-07-01"), LocalDate.parse("2024-07-31")));
            discountDAO.insertDiscount( "B002",new Discount(0.15, LocalDate.parse("2024-07-05"), LocalDate.parse("2024-08-05")));
            discountDAO.insertDiscount("M003",new Discount(0.2, LocalDate.parse("2024-07-10"), LocalDate.parse("2024-08-10")));
            discountDAO.insertDiscount("C004",new Discount(0.25, LocalDate.parse("2024-07-15"), LocalDate.parse("2024-08-15")));
            discountDAO.insertDiscount("Y005",new Discount(0.3, LocalDate.parse("2024-07-20"), LocalDate.parse("2024-08-20")));
            discountDAO.insertDiscount("B006",new Discount(0.05, LocalDate.parse("2024-07-25"), LocalDate.parse("2024-08-25")));

            System.out.println("Products and discounts initialize successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting products or discounts: " + e.getMessage());
        }
    }

    public boolean updateProductStatus(String code, ProductStatus newStatus) throws SQLException {
        return productDAO.updateProductStatus(code, newStatus);
    }

    public void insertProduct(Product new_product) throws SQLException {
        ProductDAO.insertProduct(new_product);
    }


    public void insertItemsToProduct(Product new_product, int quantity, ItemPlace itemplace, LocalDate expirationDate, ItemStatus itemstatus) throws SQLException {
        ProductDAO.insertProduct(new_product);
        for (int i = 0; i < quantity; i++) {
            Item item = new Item(itemplace, generateItemCode(), expirationDate, itemstatus);
            ItemDAO.insert(item, new_product.getProductCode());
            new_product.addItems(1, itemplace, expirationDate, itemstatus);
        }
    }

    public boolean updateItemStatus(String item_code, ItemStatus status) throws SQLException {
        return itemDAO.update(item_code, status);
    }

    public Product getProductByName(String name_product) throws SQLException {
        return productDAO.getProductByName(name_product);
    }

    public List<Product> getProductsByFilters(String category, String subCategory, Double size) throws SQLException {
        return productDAO.getAllProducts().stream()
                .filter(product -> (category.isEmpty() || product.getCategory().equalsIgnoreCase(category)))
                .filter(product -> (subCategory.isEmpty() || product.getSubCategory().equalsIgnoreCase(subCategory)))
                .filter(product -> (size == 0 || product.getSize().equals(size)))
                .collect(Collectors.toList());
    }

    public List<Product> generateCategoryReport(String category) throws SQLException {
        return productDAO.getProductsByCategory(category);
    }

    public List<Item> generateExpiredProductsReport() throws SQLException {
        return itemDAO.getAllItems().stream()
                .filter(Item::isExpired)
                .collect(Collectors.toList());
    }

    public List<Item> generateDefectiveProductsReport() throws SQLException {
        return itemDAO.getItemsByStatus(ItemStatus.Defective);
    }

    public List<Product> generateBelowMinimumReport() throws SQLException {
        return productDAO.getAllProducts().stream()
                .filter(product -> product.getTotalQuantity() < product.getMinimumQuantityForAlert())
                .collect(Collectors.toList());
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllProducts();
    }

    public List<Item> getItemsByStatus(ItemStatus status) throws SQLException {
        return itemDAO.getItemsByStatus(status);
    }

    public List<Item> getItemsByPlace(ItemPlace place) throws SQLException {
        return itemDAO.getItemsByPlace(place);
    }

    public Item getItemByCode(String itemCode) throws SQLException {
        return itemDAO.getItemByCode(itemCode);
    }

    public int getTotalProductQuantity() throws SQLException {
        return productDAO.getAllProducts().stream().mapToInt(Product::getTotalQuantity).sum();
    }

    public int getTotalProductQuantityInStore() throws SQLException {
        return productDAO.getTotalQuantityInStore();
    }

    public int getTotalProductQuantityInWarehouse() throws SQLException {
        return productDAO.getTotalQuantityInWarehouse();
    }

    public void setMinimumQuantityForProduct(String productName, int minimumQuantity) throws SQLException {
        Product product = productDAO.getProductByName(productName);
        if (product != null) {
            product.setMinimum(minimumQuantity);
            productDAO.updateProduct(product);
        } else {
            System.out.println("Product not found.");
        }
    }


    public List<Item> getAllItems() {
        itemDAO.delete("1-0");
       return itemDAO.getAllItems();
    }

    public boolean applyDiscount(String productCode, double discountPercentage, LocalDate currentDate, LocalDate endDate) {
        Product p=productDAO.getProductByCode(productCode);
        if(p!=null) {
            Discount new_discount= new Discount(discountPercentage,currentDate,endDate);
            discountDAO.insertDiscount(productCode,new_discount);
            p.applyDiscount(new_discount);
            productDAO.updateProduct(p);
            productDAO.getProductByCode(p.getProductCode());

            return true;
        }
        return false;
    }

    public List<Item> getExpiredItems() {
        itemDAO.updateExpiredProductsStatus();
        return itemDAO.getItemsByStatus(ItemStatus.Expired);
    }

    public List<Item> getDefectiveItems() {
        return itemDAO.getItemsByStatus(ItemStatus.Defective);

    }

    public List<Product> getProductsWithLowQuantity() {
        return productDAO.getProductsWithLowQuantity();    }

    public boolean setMinimumQuantity(String productCode, int newMinQuantity) {
        Product p= productDAO.getProductByCode(productCode);
        p.setMinimum(newMinQuantity);
        productDAO.updateProduct(p);
        return true;
    }

    public int getTotalQuantityInStorage() {
        int total = 0;
        List<Product> products = productDAO.getAllProducts();  // Replace with actual method to get all products
        for (Product product : products) {
            total += product.getTotalQuantity();
        }
        return total;
    }


    public Product getProductByCode(String productCode) {
        return productDAO.getProductByCode(productCode);
    }
    private String generateItemCode() {
        // Implement your item code generation logic here
        return "ITEM" + System.currentTimeMillis();
    }

    public void deleteAllProductsAndItemsAndDiscounts() {
        try {
            for (Product product : productDAO.getAllProducts()) {
                // Delete items associated with the product
                itemDAO.deleteItemsByProductCode(product.getProductCode());
                // Delete discounts associated with the product
                discountDAO.deleteDiscountsByProductCode(product.getProductCode());
                // Delete the product
                productDAO.deleteProduct(product.getProductCode());
            }
        } catch (SQLException e) {
            System.out.println("Error reset products, items, and discounts: " + e.getMessage());
        }
    }
}
