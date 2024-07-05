package BuisnessLayer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Storage {
    private List<Product> allProducts;
    private static Storage instance;

    public Storage() {
        allProducts = new ArrayList<>();
    }
    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }
    public Product getProductByName(String name_product) {
        Product getproduct = null;
        for (Product product : allProducts) {
            if (product.getProductName().equals(name_product)) {
                getproduct = product;
            }
        }
        return getproduct;
    }

    public void insertProduct(Product new_product) {
        allProducts.add(new_product);
    }
    public void insertItemstoProduct(Product new_product, int quantity,ItemPlace itemplace,LocalDate expirationDate, ItemStatus itemstatus ) throws SQLException {
        for (Product product : allProducts) {
            if (product.getProductCode().equals(new_product.getProductCode()))
            {
                product.addItems(quantity,itemplace,expirationDate,itemstatus);
                break;
            }
        }
    }
     public boolean updateItemStatus(String item_code, ItemStatus status) throws SQLException {
        for (Product product : allProducts) {
            Item item = product.getItems().get(item_code);
            if (item != null) {
                product.removeItem(item_code,status);
                return true;
            }
        }
         return false;
     }
    public List<Product> getProductsByFilters(String category, String subCategory, Double size) {
        return allProducts.stream()
                .filter(product -> (category.isEmpty() || product.getCategory().equalsIgnoreCase(category)))
                .filter(product -> (subCategory.isEmpty() || product.getSubCategory().equalsIgnoreCase(subCategory)))
                .filter(product -> (size==0 || product.getSize().equals(size)))
                .collect(Collectors.toList());
    }

    public List<Product> generateCategoryReport(String category) {
        return allProducts.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    public void checkAndProcessExpiredProducts() throws SQLException {
        for (Product product : allProducts) {
            if (product.getStatus() == ProductStatus.InStorage) {
                for (Item item : product.getItems().values()) {
                    item.isExpired();
                }}}
    }

    public List<Item> generateExpiredProductsReport() throws SQLException {
        checkAndProcessExpiredProducts();
        return allProducts.stream()
                .flatMap(product -> {
                    try {
                        return product.getItems().values().stream();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(Item::isExpired)
                .collect(Collectors.toList());
    }

    public List<Item> generateDefectiveProductsReport() {
        return allProducts.stream()
                .flatMap(product -> {
                    try {
                        return product.getItems().values().stream();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(item -> item.getStatus() == ItemStatus.Defective)
                .collect(Collectors.toList());
    }

    // Generate report for products below the minimum quantity
    public List<Product> generateBelowMinimumReport() {
        return allProducts.stream()
                .filter(product -> product.getTotalQuantity() < product.getMinimumQuantityForAlert())
                .collect(Collectors.toList());
    }

    // Apply discount to specific category or products
    public void applyDiscountToCategory(String category, Discount discount) {
        for (Product product : allProducts) {
            if (product.getCategory().equalsIgnoreCase(category)) {
                product.applyDiscount(discount);
            }
        }
    }

    public void applyDiscountToProduct(String productName, Discount discount) {
        for (Product product : allProducts) {
            if (product.getProductName().equalsIgnoreCase(productName)) {
                product.applyDiscount(discount);
            }
        }
    }

    public List<Product> getAllProducts() {
        return allProducts;
    }


    public List<Item> getItemsByStatus(ItemStatus status) throws SQLException {
        List<Item> itemsByStatus = new ArrayList<>();
        for (Product product : allProducts) {
            for (Item item : product.getItems().values()) {
                if (item.getStatus() == status) {
                    itemsByStatus.add(item);
                }
            }
        }
        return itemsByStatus;
    }

    public List<Item> getItemsByPlace(ItemPlace place) throws SQLException {
        List<Item> itemsByPlace = new ArrayList<>();
        for (Product product : allProducts) {
            for (Item item : product.getItems().values()) {
                if (item.getStored() == place) {
                    itemsByPlace.add(item);
                }
            }
        }
        return itemsByPlace;
    }

    public Item getItemByCode(String itemCode) throws SQLException {
        for (Product product : allProducts) {
            for (Item item : product.getItems().values()) {
                if (item.getItemCode().equals(itemCode)) {
                    return item;
                }
            }
        }
        return null; // Item not found
    }

    public int getTotalProductQuantity() {
        return allProducts.stream().mapToInt(Product::getTotalQuantity).sum();
    }

    public int getTotalProductQuantityInStore() {
        return allProducts.stream().mapToInt(Product::getQuantityInStore).sum();
    }

    public int getTotalProductQuantityInWarehouse() {
        return allProducts.stream().mapToInt(Product::getQuantityInWarehouse).sum();
    }

    public void setMinimumQuantityForProduct(String productName, int minimumQuantity) {
        Product product = getProductByName(productName);
        if (product != null) {
            product.setMinimum(minimumQuantity);
        } else {
            System.out.println("Product not found.");
        }
    }

}