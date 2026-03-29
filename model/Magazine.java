package model;

import java.time.LocalDate;

public class Magazine extends LibraryItem implements Borrowable {
    private String issueNumber;
    private String publisher;
    private String borrowedBy;
    private LocalDate dueDate;

    public Magazine(String id, String title, String author, int year, String issueNumber, String publisher) {
        super(id, title, author, year);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }

    @Override
    public String getType() { return "Magazine"; }

    @Override
    public String getSummary() {
        return "Magazine: " + getTitle() + " | Issue: " + issueNumber + " | Publisher: " + publisher;
    }

    public String getIssueNumber() { return issueNumber; }
    public String getPublisher() { return publisher; }

    @Override
    public boolean borrow(String userId) {
        if (!isAvailable()) return false;
        setAvailable(false);
        this.borrowedBy = userId;
        this.dueDate = LocalDate.now().plusDays(7);
        incrementBorrowCount();
        return true;
    }

    @Override
    public boolean returnItem() {
        if (isAvailable()) return false;
        setAvailable(true);
        this.borrowedBy = null;
        this.dueDate = null;
        return true;
    }

    @Override
    public LocalDate getDueDate() { return dueDate; }

    @Override
    public String getBorrowedBy() { return borrowedBy; }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + issueNumber + "|" + publisher + "|" +
               (borrowedBy != null ? borrowedBy : "null") + "|" +
               (dueDate != null ? dueDate.toString() : "null");
    }
}
