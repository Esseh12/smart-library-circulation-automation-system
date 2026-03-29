=====================================================
  Smart Library Circulation & Automation System
  COS 202 - MIVA Open University
=====================================================

HOW TO COMPILE:
  javac -d out $(find . -name "*.java")

HOW TO RUN:
  java -cp out Main

REQUIREMENTS:
  - Java 11 or higher
  - No external libraries needed (pure Java SE + Swing)

PROJECT STRUCTURE:
  Main.java               - Entry point
  model/                  - Domain classes (LibraryItem, Book, Magazine, Journal, UserAccount, LibraryDatabase)
  controller/             - Business logic (LibraryManager, SearchEngine, SortEngine, BorrowController)
  gui/                    - Swing GUI (MainWindow, ViewItemsPanel, BorrowPanel, AdminPanel, SearchSortPanel)
  utils/                  - Utilities (IDGenerator, FileHandler)

DATA FILES (auto-created on first save):
  data/items.txt          - Persisted library items
  data/users.txt          - Persisted user accounts
  reports/                - Exported report files

SLCAS_Report.docx         - Project report (2-3 pages)
