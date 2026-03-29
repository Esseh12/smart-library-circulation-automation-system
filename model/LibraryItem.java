package model;

public abstract class LibraryItem {
    private String id;
    private String title;
    private String author;
    private int year;
    private boolean available;
    private int borrowCount;

    public LibraryItem(String id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.available = true;
        this.borrowCount = 0;
    }

    public abstract String getType();
    public abstract String getSummary();

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }
    public boolean isAvailable() { return available; }
    public int getBorrowCount() { return borrowCount; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setYear(int year) { this.year = year; }
    public void setAvailable(boolean available) { this.available = available; }
    public void incrementBorrowCount() { this.borrowCount++; }

    public String toFileString() {
        return id + "|" + title + "|" + author + "|" + year + "|" + available + "|" + borrowCount + "|" + getType();
    }

    @Override
    public String toString() {
        return "[" + getType() + "] " + title + " by " + author + " (" + year + ")";
    }
}
