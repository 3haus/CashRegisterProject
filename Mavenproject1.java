/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject1;

/**
 *
 * @author tanca
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class User {
    public String username;
    public String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class Product {
    public int productId;
    public String name;
    public double price;
    public int quantity;

    public Product(int productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}

public class Mavenproject1 {
    public static ArrayList<Product> availableFoods = new ArrayList<>();
    public static ArrayList<Product> productsInCart = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static Scanner scanner = new Scanner(System.in);
    public static int productCounter = 1;
    public static boolean isAuthenticated = false;

        public static String dateTime(){
        LocalDateTime datetime = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        String formattedDate = datetime.format(myFormatObj);
        
        return formattedDate;
    }
    
    public static void main(String[] args) {
        loadUsersFromFile(); // Load previously saved users

        // If there are no users loaded, create a default admin user.
        if (users.isEmpty()) {
            User admin = new User("admin", "admin123");
            users.add(admin);
            saveUserToFile(admin);
        }
        initializeFoods();

        System.out.println("--- Sari Sari Store ---");

        if (!authenticateUser()) {
            System.out.println("Exiting system...");
            return;
        }

        System.out.println("Login successful!\n");
        startCashRegisterSystem();
    }

    // Loads users from "users.txt" if the file exists.
    public static void loadUsersFromFile() {
        File userFile = new File("users.txt");
        if (!userFile.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String password = parts[1];
                    users.add(new User(username, password));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
    }

    // Appends a new user to "users.txt"
    public static void saveUserToFile(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("users.txt", true))) {
            bw.write(user.username + "," + user.password);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }

    public static void initializeFoods() {
        availableFoods.add(new Product(productCounter++, "Piattos", 20.00, 0));
        availableFoods.add(new Product(productCounter++, "Coke 1.5L", 71.00, 0));
        availableFoods.add(new Product(productCounter++, "Lucky Me Beef Noodles", 11.00, 0));
        availableFoods.add(new Product(productCounter++, "Winston Blue 1 stick", 15.00, 0));
        availableFoods.add(new Product(productCounter++, "Powdered Tang Orange", 21.00, 0));
    }

    public static boolean authenticateUser() {
        while (!isAuthenticated) {
            System.out.println("\nDo you want to (1) Login or (2) Sign up?");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                isAuthenticated = login();
            } else if (choice.equals("2")) {
                signup();
            } else {
                System.out.println("Invalid choice, please enter 1 or 2.");
            }
        }
        return isAuthenticated;
    }

    static String asdfghjkl = "";
    
    public static boolean login() {
        System.out.print("Enter username:");
        String username = scanner.nextLine();
        System.out.print("Enter password:");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                asdfghjkl = username;
                return true;
            }
        }

        System.out.println("Invalid credentials. Please try again.");
        return false;
    }

    public static void signup() {
        System.out.println("Sign Up Page");

        String username = "";  // Initialize username
        boolean validUsername = false;

        while (!validUsername) {
            System.out.print("Enter Username (Username must be alphanumeric and 5 to 15 characters long): ");
            username = scanner.nextLine().toLowerCase();

            Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{5,15}$");
            Matcher matcher = pattern.matcher(username);
            if (matcher.matches()) {
                // Check for duplicate usernames
                boolean duplicate = false;
                for (User user : users) {
                    if (user.username.equals(username)) {
                        duplicate = true;
                        System.out.println("Username already exists. Choose another username.");
                        break;
                    }
                }
                if (!duplicate) {
                    validUsername = true;
                    System.out.println("Username Valid");
                }
            } else {
                System.out.println("Username Invalid. Try again.");
            }
        }

        String password = "";  // Initialize password
        boolean validPassword = false;

        while (!validPassword) {
            System.out.print("Enter Password (must contain at least one uppercase letter, one number, and be 8 to 20 characters long): ");
            password = scanner.nextLine();

            Pattern pattern = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,20}$");
            Matcher matcher = pattern.matcher(password);
            if (matcher.matches()) {
                validPassword = true;
                System.out.println("Password Valid");
            } else {
                System.out.println("Password Invalid. Try again.");
            }
        }

        User newUser = new User(username, password);
        users.add(newUser);
        saveUserToFile(newUser);  // Save the newly created user to file
        System.out.println("Signup successful! Please login.");
    }

    public static void startCashRegisterSystem() {
        boolean keepRunning = true;

        while (keepRunning) {
            showAvailableFoods();
            addProductsToCart();

            double totalPrice = calculateTotalPrice();
            System.out.printf("Total Price: Php%.2f%n", totalPrice);

            totalPrice = applyDiscount(totalPrice);

            double payment = acceptPayment(totalPrice);
            double change = calculateChange(payment, totalPrice);
            System.out.printf("Change: Php%.2f%n", change);

            generateReceipt(totalPrice, change);

            keepRunning = askForAnotherTransaction();
        }

        System.out.println("Thank you for using the Cash Register System!");
    }

    public static void showAvailableFoods() {
        System.out.println("\n--- Sari Sari 1 ---");
        for (Product food : availableFoods) {
            System.out.printf("%d. %s - Php%.2f%n", food.productId, food.name, food.price);
        }
    }

    public static void addProductsToCart() {
        System.out.print("\nEnter the product ID to add to the cart (or type 'done' to finish):");
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("done")) {
                break;
            }
            try {
                int productId = Integer.parseInt(input);
                Product selectedProduct = findProductById(productId);
                if (selectedProduct != null) {
                    System.out.print("Enter quantity:");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    Product productInCart = new Product(selectedProduct.productId, selectedProduct.name, selectedProduct.price, quantity);
                    productsInCart.add(productInCart);
                    System.out.printf("%s (x%d) added to your cart.%n", selectedProduct.name, quantity);
                    System.out.print("\nEnter the product ID to add to the cart (or type 'done' to finish):");

                } else {
                    System.out.println("Invalid product ID. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid product ID or type 'done'.");
            }
        }
        modifyCart();
    }

    public static Product findProductById(int id) {
        for (Product product : availableFoods) {
            if (product.productId == id) {
                return product;
            }
        }
        return null;
    }

    public static double calculateTotalPrice() {
        double totalPrice = 0;
        for (Product product : productsInCart) {
            totalPrice += product.getTotalPrice();
        }
        return totalPrice;
    }

    public static double applyDiscount(double totalPrice) {
        System.out.println("Do you have a PWD/ Senior Citizen card for discount? (yes/no)");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("yes")) {
            

                double discountAmount = (totalPrice * 20) / 100;
                totalPrice -= discountAmount;
                System.out.printf("Discount applied: Php%.2f%n", discountAmount);
        }
        return totalPrice;
    }
    static double money = 0;
    public static double acceptPayment(double totalPrice) {
        double payment;
        do {
            System.out.printf("Please enter payment amount (₱): ");
            payment = scanner.nextDouble();
            scanner.nextLine();

            if (payment < totalPrice) {
                System.out.println("Insufficient payment. Please pay the total amount.");
            }
        } while (payment < totalPrice);
        money = payment;
        return payment;
    }

    public static double calculateChange(double payment, double totalPrice) {
        return payment - totalPrice;
    }

    public static void generateReceipt(double totalPrice, double change) {
        System.out.println("\n--- Receipt ---");
        for (Product product : productsInCart) {
            System.out.printf("Product: %s | Price: Php%.2f | Quantity: %d | Total: Php%.2f%n",
                    product.name, product.price, product.quantity, product.getTotalPrice());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt",true))){
            
            writer.write(product.name);
            writer.write(", P" + product.price);
            writer.write(", Quantity: " + product.quantity);
            writer.write(", Total: " + product.getTotalPrice());
                    
            writer.newLine();
            
        } catch (IOException e){
            System.out.println("Failed to write to the file.");
            e.printStackTrace();
        }
        }
        System.out.printf("\nTotal Price: Php%.2f%n", totalPrice);
        System.out.printf("Change: Php%.2f%n", change);
        System.out.println(asdfghjkl);
        System.out.println("----------------");
        System.out.println(dateTime());
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("transactions.txt",true))){
            
            writer.write("Your Total is : P" + Double.toString(totalPrice));
            writer.write("\nEnter Cash Amount: P" + Double.toString(money));
            writer.write("\nYour change is : P" + Double.toString(change));
            writer.write("\nThank you for ordering! \nCheckout time: ");
            writer.write(dateTime());
            writer.write("\nCashier name: " + asdfghjkl);
        
            writer.newLine();
            
        } catch (IOException e){
            System.out.println("Failed to write to the file.");
            e.printStackTrace();
        }
    }

    public static boolean askForAnotherTransaction() {
        productsInCart.clear();
        System.out.println("Do you want to perform another transaction? (yes/no):");
        return scanner.nextLine().equalsIgnoreCase("yes");
    }
        public static void modifyCart() {
        while (true) {
            if (productsInCart.isEmpty()) {
                System.out.println("Your cart is empty.");
                break;
            }
            System.out.println("\n--- Your Current Cart ---");
            int index = 1;
            for (Product p : productsInCart) {
                System.out.printf("%d. %s - Quantity: %d - Price: Php%.2f%n", index, p.name, p.quantity, p.price);
                index++;
            }
            System.out.println("\nType 'edit' to change quantity, 'delete' to remove an item, or 'done' to finish modifications:");
            String action = scanner.nextLine();

            if (action.equalsIgnoreCase("done")) {
                break;
            } else if (action.equalsIgnoreCase("edit")) {
                System.out.println("Enter the item number you want to edit:");
                try {
                    int itemNumber = Integer.parseInt(scanner.nextLine());
                    if (itemNumber < 1 || itemNumber > productsInCart.size()) {
                        System.out.println("Invalid item number. Please try again.");
                    } else {
                        Product productToEdit = productsInCart.get(itemNumber - 1);
                        System.out.println("Enter the new quantity for " + productToEdit.name + ":");
                        int newQuantity = Integer.parseInt(scanner.nextLine());
                        if (newQuantity > 0) {
                            productToEdit.quantity = newQuantity;
                            System.out.println("Quantity updated for " + productToEdit.name + ".");
                        } else {
                            System.out.println("Quantity must be greater than 0.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else if (action.equalsIgnoreCase("delete")) {
                System.out.println("Enter the item number you want to delete:");
                try {
                    int itemNumber = Integer.parseInt(scanner.nextLine());
                    if (itemNumber < 1 || itemNumber > productsInCart.size()) {
                        System.out.println("Invalid item number. Please try again.");
                    } else {
                        Product removedProduct = productsInCart.remove(itemNumber - 1);
                        System.out.println(removedProduct.name + " has been removed from your cart.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }
            } else {
                System.out.println("Invalid option. Please choose 'edit', 'delete', or 'done'.");
            }
        }
    }
        
}