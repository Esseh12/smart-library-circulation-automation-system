package controller;

import model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowController {
    private LibraryDatabase db;

    public BorrowController(LibraryDatabase db) {
        this.db = db;
    }

    public String borrowItem(String itemId, String userId) {
        LibraryItem item = db.findById(itemId);
        UserAccount user = db.findUserById(userId);

        if (item == null) return "Item not found.";
        if (user == null) return "User not found.";
        if (!(item instanceof Borrowable)) return "This item cannot be borrowed.";

        Borrowable borrowable = (Borrowable) item;

        if (!item.isAvailable()) {
            db.enqueueReservation(itemId, userId);
            return "Item unavailable. You have been added to the waitlist.";
        }

        borrowable.borrow(userId);
        user.addBorrow(itemId);
        return "Successfully borrowed: " + item.getTitle() +
               " | Due: " + borrowable.getDueDate();
    }

    public String returnItem(String itemId, String userId) {
        LibraryItem item = db.findById(itemId);
        UserAccount user = db.findUserById(userId);

        if (item == null) return "Item not found.";
        if (user == null) return "User not found.";
        if (!(item instanceof Borrowable)) return "This item is not borrowable.";
        if (!user.hasBorrowed(itemId)) return "This user did not borrow this item.";

        Borrowable borrowable = (Borrowable) item;
        borrowable.returnItem();
        user.removeBorrow(itemId);

        String[] nextReservation = db.dequeueNextReservation(itemId);
        if (nextReservation != null) {
            return "Returned successfully. Notified next user: " + nextReservation[1];
        }
        return "Returned successfully: " + item.getTitle();
    }

    public List<LibraryItem> getOverdueItems() {
        List<LibraryItem> overdue = new ArrayList<>();
        for (LibraryItem item : db.getItems()) {
            if (item instanceof Borrowable) {
                Borrowable b = (Borrowable) item;
                if (!item.isAvailable() && b.getDueDate() != null &&
                    b.getDueDate().isBefore(LocalDate.now())) {
                    overdue.add(item);
                }
            }
        }
        return overdue;
    }

    public double computeOverdueFine(LibraryItem item) {
        return computeOverdueFineRecursive(item, LocalDate.now());
    }

    private double computeOverdueFineRecursive(LibraryItem item, LocalDate today) {
        if (!(item instanceof Borrowable)) return 0.0;
        Borrowable b = (Borrowable) item;
        if (b.getDueDate() == null || !b.getDueDate().isBefore(today)) return 0.0;
        long daysOverdue = ChronoUnit.DAYS.between(b.getDueDate(), today);
        return daysOverdue * 50.0;
    }

    public List<UserAccount> getUsersWithOverdueItems() {
        List<LibraryItem> overdueItems = getOverdueItems();
        List<UserAccount> result = new ArrayList<>();
        for (LibraryItem item : overdueItems) {
            if (item instanceof Borrowable) {
                String userId = ((Borrowable) item).getBorrowedBy();
                if (userId != null) {
                    UserAccount user = db.findUserById(userId);
                    if (user != null && !result.contains(user)) result.add(user);
                }
            }
        }
        return result;
    }
}
