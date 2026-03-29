Smart Library Circulation & Automation System (SLCAS)
A fully functional university library management desktop application built in Java Swing for COS 202 at MIVA Open University.
What it does
SLCAS allows librarians and administrators to manage a library catalogue end-to-end — adding books, magazines, and journals, registering users, processing borrowing and returns, managing waitlists, searching and sorting the collection, tracking overdue items with automatic fine calculation, and generating reports.
Tech Stack

Language: Java (SE 11+)
GUI: Java Swing
Persistence: Plain text file storage

Key Features

Add, delete, and undo management of Books, Magazines, and Journals
Borrow and return workflow with automatic due dates and reservation queue
Overdue detection with ₦50/day fine computation
Search using Linear, Binary, or Recursive algorithms — user selectable
Sort by title, author, or year using Selection, Insertion, Merge, or Quick Sort — user selectable
Top 5 most frequently accessed items cache
Report generation with file export
Timer-triggered background overdue reminders

Concepts Demonstrated

Abstract classes, interfaces, inheritance, and polymorphism
ArrayList, Queue, Stack, and fixed-size array cache
Recursive algorithms (category count, fine computation, search)
Event-driven GUI programming with Swing (BorderLayout, GridBagLayout, CardLayout, JTabbedPane, JTable, JFileChooser, Timer)
MVC-style package structure

Project Structure

model/       → Domain classes
controller/  → Business logic
gui/         → Swing panels and windows
utils/       → File I/O and ID generation

How to Run
bashmkdir out
javac -d out $(find . -name "*.java")   # Linux/Mac
java -cp out Main
powershellmkdir out
javac -d out (Get-ChildItem -Recurse -Filter "*.java" | ForEach-Object { $_.FullName })  # Windows
java -cp out Main
