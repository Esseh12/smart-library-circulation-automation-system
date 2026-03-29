package controller;

import model.*;
import utils.FileHandler;
import utils.IDGenerator;
import java.util.ArrayList;
import java.util.List;

public class LibraryManager {
    private LibraryDatabase db;
    private SearchEngine searchEngine;
    private SortEngine sortEngine;
    private BorrowController borrowController;

    public LibraryManager() {
        this.db = new LibraryDatabase();
        this.searchEngine = new SearchEngine();
        this.sortEngine = new SortEngine();
        this.borrowController = new BorrowController(db);
        loadData();
    }

    public LibraryDatabase getDb() { return db; }
    public SearchEngine getSearchEngine() { return searchEngine; }
    public SortEngine getSortEngine() { return sortEngine; }
    public BorrowController getBorrowController() { return borrowController; }

    public void addBook(String title, String author, int year, String isbn, String genre) {
        String id = IDGenerator.generateItemId("Book");
        Book book = new Book(id, title, author, year, isbn, genre);
        db.addItem(book);
        saveData();
    }

    public void addMagazine(String title, String author, int year, String issue, String publisher) {
        String id = IDGenerator.generateItemId("Magazine");
        Magazine mag = new Magazine(id, title, author, year, issue, publisher);
        db.addItem(mag);
        saveData();
    }

    public void addJournal(String title, String author, int year, String volume, String field) {
        String id = IDGenerator.generateItemId("Journal");
        Journal journal = new Journal(id, title, author, year, volume, field);
        db.addItem(journal);
        saveData();
    }

    public boolean deleteItem(String itemId) {
        boolean result = db.removeItem(itemId);
        if (result) saveData();
        return result;
    }

    public String undoLastAction() {
        Object[] action = db.undoLastAction();
        if (action == null) return "Nothing to undo.";
        saveData();
        return "Undone: " + action[0] + " - " + ((LibraryItem) action[1]).getTitle();
    }

    public void addUser(String name, String email) {
        String id = IDGenerator.generateUserId();
        UserAccount user = new UserAccount(id, name, email);
        db.addUser(user);
        saveData();
    }

    public boolean deleteUser(String userId) {
        boolean result = db.removeUser(userId);
        if (result) saveData();
        return result;
    }

    public String borrow(String itemId, String userId) {
        String result = borrowController.borrowItem(itemId, userId);
        saveData();
        return result;
    }

    public String returnItem(String itemId, String userId) {
        String result = borrowController.returnItem(itemId, userId);
        saveData();
        return result;
    }

    public List<LibraryItem> search(String query, String field) {
        return searchEngine.linearSearch(db.getItems(), query, field);
    }

    public List<LibraryItem> sortItems(SortEngine.SortField field, SortEngine.SortAlgorithm algo) {
        return sortEngine.sort(db.getItems(), field, algo);
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SMART LIBRARY SYSTEM REPORT ===\n\n");

        sb.append("--- Most Borrowed Items ---\n");
        List<LibraryItem> items = new ArrayList<>(db.getItems());
        items.sort((a, b) -> b.getBorrowCount() - a.getBorrowCount());
        int limit = Math.min(5, items.size());
        for (int i = 0; i < limit; i++) {
            sb.append((i + 1) + ". " + items.get(i).getTitle() +
                      " [" + items.get(i).getBorrowCount() + " borrows]\n");
        }

        sb.append("\n--- Category Distribution ---\n");
        sb.append("Books: " + db.countByType("Book") + "\n");
        sb.append("Magazines: " + db.countByType("Magazine") + "\n");
        sb.append("Journals: " + db.countByType("Journal") + "\n");

        sb.append("\n--- Users with Overdue Items ---\n");
        List<UserAccount> overdueUsers = borrowController.getUsersWithOverdueItems();
        if (overdueUsers.isEmpty()) {
            sb.append("No overdue items.\n");
        } else {
            for (UserAccount u : overdueUsers) {
                sb.append("- " + u.getName() + " (" + u.getUserId() + ")\n");
            }
        }

        return sb.toString();
    }

    public void saveData() {
        FileHandler.saveItems(db.getItems());
        FileHandler.saveUsers(db.getUsers());
    }

    private void loadData() {
        ArrayList<LibraryItem> items = FileHandler.loadItems();
        ArrayList<UserAccount> users = FileHandler.loadUsers();
        for (LibraryItem item : items) db.getItems().add(item);
        for (UserAccount user : users) db.getUsers().add(user);
    }

    public void processAllLibraryItems(List<LibraryItem> items) {
        for (LibraryItem item : items) {
            System.out.println(item.getSummary());
        }
    }
}
