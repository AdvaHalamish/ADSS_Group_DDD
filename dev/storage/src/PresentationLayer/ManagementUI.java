package PresentationLayer;

import BuisnessLayer.*;
import DataAccessLayer.ItemDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ManagementUI {
    private ManagementController managementController;

    public ManagementUI(ManagementController managementController) {
        this.managementController = managementController;
    }

    public void displayMenu(Scanner scanner) throws SQLException {
        int choice = 0;
        do {
            System.out.println("\nManagement Menu");
            System.out.println("1. Show All Items");
            System.out.println("2. Show All Products");
            System.out.println("3. Show Specific Items (By Category, Status, or Place)");
            System.out.println("4. Details about a specific item");
            System.out.println("5. Apply Discount");
            System.out.println("6. Generate Report");
            System.out.println("7. Set Minimum Quantity For Product");
            System.out.println("8. View Total Quantity in Storage");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer choice.");
                scanner.nextLine(); // Consume invalid input
                continue; // Continue to next iteration of the loop
            }

            switch (choice) {
                case 1:
                    showAllItems();
                    break;
                case 2:
                    showAllProducts();
                    break;
                case 3:
                    showSpecificItemsMenu(scanner);
                    break;
                case 4:
                    showItemDetails(scanner);
                    break;
                case 5:
                    applyDiscountMenu(scanner);
                    break;
                case 6:
                    generateReportMenu(scanner);
                    break;
                case 7:
                    setMinimumQuantityForProduct(scanner);
                    break;
                case 8:
                    displayTotalQuantityInStorage();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                    break;
            }
        } while (choice != 9);
    }

    private void showSpecificItemsMenu(Scanner scanner) throws SQLException {
        System.out.println("\nShow Specific Items");
        System.out.println("1. By Category, Sub Category and Size ");
        System.out.println("2. By Status");
        System.out.println("3. By Place");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                showItemsInCategoriesMenu(scanner);
                break;
            case 2:
                showItemsByStatus(scanner);
                break;
            case 3:
                showItemsByPlace(scanner);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private void showItemsInCategoriesMenu(Scanner scanner) throws SQLException {
        System.out.print("\nEnter category (leave blank if no filter): ");
        String category = scanner.nextLine();
        System.out.print("Enter sub-category (leave blank if no filter): ");
        String subCategory = scanner.nextLine();
        System.out.print("Enter size (leave blank if no filter): ");
        String sizeInput = scanner.nextLine();

        Double size = null;
        if (!sizeInput.isEmpty()) {
            try {
                size = Double.parseDouble(sizeInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid size format. Must be a number.");
                return;
            }
        }
        List<List<Item>> items=new ArrayList<>();
        List<Product> products = managementController.getProductsByFilters(category, subCategory, size);
        for(Product product : products) {
            items.add(ItemDAO.getInstance().getItemsByProductCode(product.getProductCode()));

        }
        if (items.isEmpty()) {
            System.out.println("No items found matching the criteria.");
        } else {
            for (List<Item> item : items) {
                System.out.println(item);
            }
        }
    }

    private void showItemsByStatus(Scanner scanner) {
        System.out.print("Enter status (Available, Defective, Sold, Expired): ");
        String statusString = scanner.nextLine();

        try {
            ItemStatus status = ItemStatus.valueOf(statusString);
            List<Item> items = managementController.getItemsByStatus(status);
            if (items.isEmpty()) {
                System.out.println("No items found with status " + status + ".");
            } else {
                for (Item item : items) {
                    System.out.println(item);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showItemsByPlace(Scanner scanner) {
        System.out.print("Enter place (Store, Warehouse): ");
        String placeString = scanner.nextLine();

        try {
            ItemPlace place = ItemPlace.valueOf(placeString);
            List<Item> items = managementController.getItemsByPlace(place);
            if (items.isEmpty()) {
                System.out.println("No items found in " + place + ".");
            } else {
                for (Item item : items) {
                    System.out.println(item);
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid place.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAllItems() {
        List<Item> items = managementController.getAllItems();
        if (items.isEmpty()) {
            System.out.println("No items found.");
        } else {
            for (Item item : items) {
                System.out.println(item);
            }
        }
    }

    private void showAllProducts() throws SQLException {
        List<Product> products = managementController.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (Product product : products) {
                System.out.println(product);
            }
        }
    }

    private void showItemDetails(Scanner scanner) throws SQLException {
        System.out.print("Enter item code: ");
        String itemCode = scanner.nextLine();
        Item item = managementController.getItemByCode(itemCode);
        if (item != null) {
            System.out.println("Item details: " + item);
        } else {
            System.out.println("Item not found.");
        }
    }

    private void applyDiscountMenu(Scanner scanner) throws SQLException {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();
        System.out.print("Enter discount percentage: ");
        double discountPercentage = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        if (discountPercentage < 0 || discountPercentage > 100) {
            System.out.println("Invalid discount percentage. Must be between 0 and 100.");
            return;
        }

        System.out.print("Enter discount start date (YYYY-MM-DD): ");
        String startDateInput = scanner.nextLine();
        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startDateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        System.out.print("Enter discount end date (YYYY-MM-DD): ");
        String endDateInput = scanner.nextLine();
        LocalDate endDate;
        try {
            endDate = LocalDate.parse(endDateInput);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        if (endDate.isBefore(startDate)) {
            System.out.println("End date cannot be before start date.");
            return;
        }

        if (managementController.applyDiscount(productCode, discountPercentage, startDate, endDate)) {
            System.out.println("Discount applied successfully.");
        } else {
            System.out.println("Product not found or already has an active discount.");
        }
    }

    private void generateReportMenu(Scanner scanner) {
        System.out.println("\nGenerate Report");
        System.out.println("1. Expired Products");
        System.out.println("2. Defective Products");
        System.out.println("3. Products Below Minimum Quantity");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                generateExpiredProductsReport();
                break;
            case 2:
                generateDefectiveProductsReport();
                break;
            case 3:
                generateProductsBelowMinimumQuantityReport();
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                break;
        }
    }

    private void generateExpiredProductsReport() {
        List<Item> expiredItems = managementController.getExpiredItems();
        if (expiredItems.isEmpty()) {
            System.out.println("No expired products found.");
        } else {
            for (Item item : expiredItems) {
                System.out.println(item);
            }
        }
    }

    private void generateDefectiveProductsReport() {
        List<Item> defectiveItems = managementController.getDefectiveItems();
        if (defectiveItems.isEmpty()) {
            System.out.println("No defective items found.");
        } else {
            for (Item item : defectiveItems) {
                System.out.println(item);
            }
        }
    }

    private void generateProductsBelowMinimumQuantityReport() {
        List<Product> productsBelowMinQuantity = managementController.getProductsWithLowQuantity();
        if (productsBelowMinQuantity.isEmpty()) {
            System.out.println("No products found below minimum quantity.");
        } else {
            for (Product product : productsBelowMinQuantity) {
                System.out.println(product);
            }
        }
    }

    private void setMinimumQuantityForProduct(Scanner scanner) {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();
        System.out.print("Enter new minimum quantity: ");
        int newMinQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (managementController.setMinimumQuantity(productCode, newMinQuantity)) {
            System.out.println("Minimum quantity updated successfully.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void displayTotalQuantityInStorage() {
        int totalAmount = managementController.getTotalQuantityInStorage();
        System.out.println("Total items Quantity in storage: " + totalAmount);
    }
}
