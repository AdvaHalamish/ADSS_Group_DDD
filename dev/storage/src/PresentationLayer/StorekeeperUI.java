package PresentationLayer;

import BuisnessLayer.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Scanner;

public class StorekeeperUI {
    private ManagementController ManagementController;

    public StorekeeperUI(ManagementController ManagementController) {
        this.ManagementController = ManagementController;
    }

    public void displayMenu(Scanner scanner) throws SQLException {
        int choice = 0;
        do {
            System.out.println("\nStorekeeper Menu");
            System.out.println("1. Insert New Product");
            System.out.println("2. Insert New Items for Product");
            System.out.println("3. Update or remove Item Status");
            System.out.println("4. Update or remove Product Status");
            System.out.println("5. Exit");
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
                    insertNewProduct(scanner);
                    break;
                case 2:
                    insertNewItemsForProduct(scanner);
                    break;
                case 3:
                    updateItemStatus(scanner);
                    break;
                case 4:
                    updateProductStatus(scanner);
                    break;
                case 5:
                    System.out.println("Exiting Storekeeper Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
    }

    private void insertNewItemsForProduct(Scanner scanner) throws SQLException {
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();
        Product product = ManagementController.getProductByCode(productCode);
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.print("Enter quantity of items to add: ");
        int addQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter item place (Store or Warehouse): ");
        String itemPlaceInput = scanner.nextLine();
        ItemPlace itemPlace = ItemPlace.valueOf(itemPlaceInput);

        System.out.print("Enter expiration date (yyyy-mm-dd): ");
        String expirationDateString = scanner.nextLine();
        LocalDate expirationDate = LocalDate.parse(expirationDateString);

        System.out.print("Enter item status (Available, Defective, Sold, Expired): ");
        String itemStatusInput = scanner.nextLine();
        ItemStatus itemStatus = ItemStatus.valueOf(itemStatusInput);

        product.addItems(addQuantity, itemPlace, expirationDate, itemStatus);
        ManagementController.insertItemsToProduct(product,addQuantity, itemPlace, expirationDate, itemStatus);
        System.out.println("Items added successfully to product " + productCode);
    }

    private void updateProductStatus(Scanner scanner) throws SQLException {
        System.out.print("Enter product code to update: ");
        String code = scanner.nextLine();
        System.out.print("Enter new status for the product (InStorage/NotInStorage): ");
        String statusString = scanner.nextLine();
        ProductStatus newStatus;
        try {
            newStatus = ProductStatus.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Must be InStorage or NotInStorage.");
            return;
        }

        if (ManagementController.updateProductStatus(code, newStatus)) {
            System.out.println("Product status updated successfully.");
        } else {
            System.out.println("Failed to update product status.");
        }
    }
    private void insertNewProduct(Scanner scanner) throws SQLException {
        System.out.print("Enter manufacturer: ");
        String manufacturer = scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Enter product name: ");
        String productName = scanner.nextLine();
        System.out.print("Enter sub-category: ");
        String subCategory = scanner.nextLine();
        System.out.print("Enter size: ");
        double size = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter product code: ");
        String productCode = scanner.nextLine();
        System.out.print("Enter purchase price: ");
        double purchasePrice = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter minimum quantity for alert: ");
        int minQuantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter item place (Store or Warehouse): ");
        String itemPlaceInput = scanner.nextLine();
        ItemPlace itemPlace = ItemPlace.valueOf(itemPlaceInput); // Validate later

        System.out.print("Enter expiration date (yyyy-mm-dd): ");
        String expirationDateString = scanner.nextLine();
        LocalDate expirationDate = LocalDate.parse(expirationDateString);
        Product product = new Product(manufacturer, category, productName, subCategory, productCode,
                purchasePrice,purchasePrice,size, quantity, minQuantity, itemPlace, expirationDate);

        ManagementController.insertProduct(product);
        System.out.println("Product inserted successfully.");
    }


    private void updateItemStatus(Scanner scanner) throws SQLException {
        System.out.print("Enter item code to update: ");
        String code = scanner.nextLine();
        System.out.print("Enter new status for the item: (Defective, Sold, Expired) ");
        String statusString = scanner.nextLine();

        if (isValidStatus(statusString)) {
            ItemStatus status = ItemStatus.valueOf(statusString);
            if (ManagementController.updateItemStatus(code, status)) {
                System.out.println("Item status updated.");
            } else {
                System.out.println("Item not found.");
            }
        } else {
            System.out.println("Invalid status.");
        }
    }

    private boolean isValidStatus(String statusInput) {
        try {
            ItemStatus.valueOf(statusInput);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}