package model;

import java.time.LocalDate;

public interface Borrowable {
    boolean borrow(String userId);
    boolean returnItem();
    LocalDate getDueDate();
    String getBorrowedBy();
}
