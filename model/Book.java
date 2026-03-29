package model;

import java.time.LocalDate;

public class Book extends LibraryItem implements Borrowable {
    private String isbn;
    private String genre;
    private String borrowedBy;
    private LocalDate dueDate;

    public Book(String id, String title, String author, int year, String isbn, String genre) {
        super(id, title, author, year);
        this.isbn = isbn;
        this.genre = genre;
    }

    @Override
    public String getType() { return "Book"; }

    @Override
    public String getSummary() {
        return "Book: " + getTitle() + " | ISBN: " + isbn + " | Genre: " + genre;
    }

    public String getIsbn() { return isbn; }
    public String getGenre() { return genre; }

    @Override
    public boolean borrow(String userId) {
        if (!isAvailable()) return false;
        setAvailable(false);
        this.borrowedBy = userId;
        this.dueDate = LocalDate.now().plusDays(14);
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
        return super.toFileString() + "|" + isbn + "|" + genre + "|" +
               (borrowedBy != null ? borrowedBy : "null") + "|" +
               (dueDate != null ? dueDate.toString() : "null");
    }
}
