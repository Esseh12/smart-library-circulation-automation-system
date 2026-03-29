package model;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private String userId;
    private String name;
    private String email;
    private List<String> borrowingHistory;
    private List<String> currentBorrows;

    public UserAccount(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.borrowingHistory = new ArrayList<>();
        this.currentBorrows = new ArrayList<>();
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getBorrowingHistory() { return borrowingHistory; }
    public List<String> getCurrentBorrows() { return currentBorrows; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public void addBorrow(String itemId) {
        currentBorrows.add(itemId);
        borrowingHistory.add(itemId);
    }

    public void removeBorrow(String itemId) {
        currentBorrows.remove(itemId);
    }

    public boolean hasBorrowed(String itemId) {
        return currentBorrows.contains(itemId);
    }

    public String toFileString() {
        String history = String.join(",", borrowingHistory);
        String current = String.join(",", currentBorrows);
        return userId + "|" + name + "|" + email + "|" +
               (history.isEmpty() ? "none" : history) + "|" +
               (current.isEmpty() ? "none" : current);
    }

    @Override
    public String toString() {
        return name + " (" + userId + ")";
    }
}
