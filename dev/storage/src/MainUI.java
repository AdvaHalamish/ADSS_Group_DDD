import BuisnessLayer.Storage;
import DataAccessLayer.Database;
import BuisnessLayer.ManagementController;
import PresentationLayer.ManagementUI;
import PresentationLayer.StorekeeperUI;
import PresentationLayer.UserUI;

import java.io.*;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;



public class MainUI {
    //private static Storage storage;
    private static ManagementController managementController;


    private static boolean createDB() {
        // if directory does not exist, create it
        File directory = new File("release/");
//        if (!directory.exists())
//            directory.mkdir();
//        directory = new File("src/main/");
//        if (!directory.exists())
//            directory.mkdir();
//        directory = new File("release/mydatabase.db");
//        if (!directory.exists())
//            directory.mkdir();

        String dbPath = "mydatabase.db";
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try (InputStream in = MainUI.class.getResourceAsStream("mydatabase.db");
                 OutputStream out = new FileOutputStream(dbPath)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static void main(String[] args) throws SQLException {

        createDB();

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        // Choose storage type first
        //chooseStorageType(scanner); //TODO
        managementController = new ManagementController(true);

        do {
            System.out.println("\nMain Menu");
            System.out.println("1. Store Keeper Menu");
            System.out.println("2. Management Menu");
            System.out.println("3. User Menu");
            System.out.println("4. Exit");
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
                    StorekeeperUI storekeeperUI = new StorekeeperUI(managementController);
                    storekeeperUI.displayMenu(scanner);
                    break;
                case 2:
                    ManagementUI departmentMenu = new ManagementUI(managementController);
                    departmentMenu.displayMenu(scanner);
                    break;
                case 3:
                    UserUI userUI = new UserUI(managementController);
                    userUI.displayMenu(scanner);
                    break;
                case 4:
                    System.out.println("Exiting Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);

        // Close resources
        scanner.close();
        Database.closeConnection(); // Assuming Database class manages the database connection
    }

    private static void chooseStorageType(Scanner scanner) throws SQLException {
        int storageChoice = 0;
        do {
            System.out.println("\nChoose Storage Type");
            System.out.println("1. Use Empty Storage");
            System.out.println("2. Use Pre-populated Database");
            System.out.print("Enter your choice: ");

            try {
                storageChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer choice.");
                scanner.nextLine(); // Consume invalid input
                continue; // Continue to next iteration of the loop
            }

            switch (storageChoice) {
                case 1:
                    initializeEmptyStorage();
                    break;
                case 2:
                    initializeDatabaseStorage();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (storageChoice < 1 || storageChoice > 2);
    }

    private static void initializeEmptyStorage() throws SQLException {
        // Initialize empty storage (not implemented in this example)
        managementController = new ManagementController(false);

    }

    private static void initializeDatabaseStorage() throws SQLException {
        // Initialize storage with database data
        managementController = new ManagementController(true);
    }
}
