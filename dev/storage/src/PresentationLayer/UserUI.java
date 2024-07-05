package PresentationLayer;

import BuisnessLayer.Item;
import BuisnessLayer.ItemStatus;
import BuisnessLayer.ManagementController;
import BuisnessLayer.Product;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserUI {
    private ManagementController managementController;

    public UserUI(ManagementController managementController) {
        this.managementController = managementController;
    }

    public void displayMenu(Scanner scanner) throws SQLException {
        int choice = 0;
        do {
            System.out.println("\nMain Menu:");
            System.out.println("1. Show all Available products");
            System.out.println("2. Details about a specific Product");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
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
                    showAvailableProducts();
                    break;
                case 2:
                    showProductDetails(scanner);
                    break;
                case 3:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    private void showAvailableProducts() throws SQLException {
        System.out.println("Available products in storage:");
        for (Product product : managementController.getAllProducts()) {
            for (Item item : product.getItems().values()) {
                if (item.getStatus() == ItemStatus.Available) {
                    System.out.println(product.getProductName());
                    break;
                }
            }
        }
    }

    private void showProductDetails(Scanner scanner) throws SQLException {
        System.out.print("Enter Product name: ");
        String productName = scanner.nextLine();
        Product product = managementController.getProductByName(productName);
        if (product != null) {
            System.out.println("Product details: " + product);
        } else {
            System.out.println("Product not found.");
        }
    }
}
