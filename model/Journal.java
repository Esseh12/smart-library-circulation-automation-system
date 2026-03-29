package model;

import java.time.LocalDate;

public class Journal extends LibraryItem implements Borrowable {
    private String volume;
    private String field;
    private String borrowedBy;
    private LocalDate dueDate;

    public Journal(String id, String title, String author, int year, String volume, String field) {
        super(id, title, author, year);
        this.volume = volume;
        this.field = field;
    }

    @Override
    public String getType() { return "Journal"; }

    @Override
    public String getSummary() {
        return "Journal: " + getTitle() + " | Volume: " + volume + " | Field: " + field;
    }

    public String getVolume() { return volume; }
    public String getField() { return field; }

    @Override
    public boolean borrow(String userId) {
        if (!isAvailable()) return false;
        setAvailable(false);
        this.borrowedBy = userId;
        this.dueDate = LocalDate.now().plusDays(21);
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
        return super.toFileString() + "|" + volume + "|" + field + "|" +
               (borrowedBy != null ? borrowedBy : "null") + "|" +
               (dueDate != null ? dueDate.toString() : "null");
    }
}
