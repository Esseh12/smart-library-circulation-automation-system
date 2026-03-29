package utils;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String ITEMS_FILE = "data/items.txt";
    private static final String USERS_FILE = "data/users.txt";

    public static void saveItems(List<LibraryItem> items) {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ITEMS_FILE))) {
            for (LibraryItem item : items) {
                writer.write(item.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving items: " + e.getMessage());
        }
    }

    public static ArrayList<LibraryItem> loadItems() {
        ArrayList<LibraryItem> items = new ArrayList<>();
        File file = new File(ITEMS_FILE);
        if (!file.exists()) return items;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LibraryItem item = parseItem(line);
                if (item != null) items.add(item);
            }
        } catch (IOException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
        return items;
    }

    private static LibraryItem parseItem(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            String id = parts[0];
            String title = parts[1];
            String author = parts[2];
            int year = Integer.parseInt(parts[3]);
            boolean available = Boolean.parseBoolean(parts[4]);
            int borrowCount = Integer.parseInt(parts[5]);
            String type = parts[6];

            LibraryItem item;
            switch (type) {
                case "Book":
                    String isbn = parts.length > 7 ? parts[7] : "";
                    String genre = parts.length > 8 ? parts[8] : "";
                    Book book = new Book(id, title, author, year, isbn, genre);
                    book.setAvailable(available);
                    if (!available && parts.length > 9 && !"null".equals(parts[9])) {
                        book.borrow(parts[9]);
                        book.setAvailable(false);
                    }
                    item = book;
                    break;
                case "Magazine":
                    String issue = parts.length > 7 ? parts[7] : "";
                    String pub = parts.length > 8 ? parts[8] : "";
                    Magazine mag = new Magazine(id, title, author, year, issue, pub);
                    mag.setAvailable(available);
                    item = mag;
                    break;
                case "Journal":
                    String vol = parts.length > 7 ? parts[7] : "";
                    String field = parts.length > 8 ? parts[8] : "";
                    Journal journal = new Journal(id, title, author, year, vol, field);
                    journal.setAvailable(available);
                    item = journal;
                    break;
                default:
                    return null;
            }
            for (int i = 0; i < borrowCount; i++) item.incrementBorrowCount();
            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public static void saveUsers(List<UserAccount> users) {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (UserAccount user : users) {
                writer.write(user.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    public static ArrayList<UserAccount> loadUsers() {
        ArrayList<UserAccount> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                UserAccount user = parseUser(line);
                if (user != null) users.add(user);
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    private static UserAccount parseUser(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            UserAccount user = new UserAccount(parts[0], parts[1], parts[2]);
            if (parts.length > 3 && !"none".equals(parts[3])) {
                for (String id : parts[3].split(",")) user.getBorrowingHistory().add(id);
            }
            if (parts.length > 4 && !"none".equals(parts[4])) {
                for (String id : parts[4].split(",")) user.getCurrentBorrows().add(id);
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static void exportReport(String content, String filename) {
        new File("reports").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("reports/" + filename))) {
            writer.write(content);
        } catch (IOException e) {
            System.err.println("Error exporting report: " + e.getMessage());
        }
    }
}
