package gui;

import controller.LibraryManager;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ViewItemsPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTable cacheTable;
    private DefaultTableModel cacheModel;
    private JComboBox<String> filterTypeCombo;
    private JLabel totalLabel;

    public ViewItemsPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Filter by type:"));
        filterTypeCombo = new JComboBox<>(new String[]{"All", "Book", "Magazine", "Journal"});
        filterTypeCombo.setToolTipText("Filter items by type");
        filterTypeCombo.addActionListener(e -> refresh());
        topBar.add(filterTypeCombo);

        totalLabel = new JLabel("Total: 0");
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(totalLabel);

        String[] columns = {"ID", "Type", "Title", "Author", "Year", "Available", "Borrows"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(24);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.setDefaultRenderer(Object.class, new ItemTableRenderer());
        itemTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        itemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) showItemDetails();
            }
        });

        JScrollPane tableScroll = new JScrollPane(itemTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Library Catalogue"));

        String[] cacheColumns = {"#", "Title", "Author", "Borrows"};
        cacheModel = new DefaultTableModel(cacheColumns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cacheTable = new JTable(cacheModel);
        cacheTable.setRowHeight(22);
        cacheTable.setBackground(new Color(255, 250, 230));
        JScrollPane cacheScroll = new JScrollPane(cacheTable);
        cacheScroll.setPreferredSize(new Dimension(0, 160));
        cacheScroll.setBorder(BorderFactory.createTitledBorder("Most Frequently Accessed (Top 5)"));

        add(topBar, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
        add(cacheScroll, BorderLayout.SOUTH);
    }

    public void refresh() {
        tableModel.setRowCount(0);
        String filter = (String) filterTypeCombo.getSelectedItem();
        List<LibraryItem> items = manager.getDb().getItems();

        int count = 0;
        for (LibraryItem item : items) {
            if ("All".equals(filter) || item.getType().equals(filter)) {
                tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getType(),
                    item.getTitle(),
                    item.getAuthor(),
                    item.getYear(),
                    item.isAvailable() ? "Yes" : "No",
                    item.getBorrowCount()
                });
                count++;
            }
        }
        totalLabel.setText("Total: " + count);

        cacheModel.setRowCount(0);
        LibraryItem[] cache = manager.getDb().getFrequentCache();
        for (int i = 0; i < cache.length; i++) {
            if (cache[i] != null) {
                cacheModel.addRow(new Object[]{
                    i + 1, cache[i].getTitle(), cache[i].getAuthor(), cache[i].getBorrowCount()
                });
            }
        }
    }

    private void showItemDetails() {
        int row = itemTable.getSelectedRow();
        if (row < 0) return;
        String id = (String) tableModel.getValueAt(row, 0);
        LibraryItem item = manager.getDb().findById(id);
        if (item == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(item.getId()).append("\n");
        sb.append("Type: ").append(item.getType()).append("\n");
        sb.append("Title: ").append(item.getTitle()).append("\n");
        sb.append("Author: ").append(item.getAuthor()).append("\n");
        sb.append("Year: ").append(item.getYear()).append("\n");
        sb.append("Available: ").append(item.isAvailable() ? "Yes" : "No").append("\n");
        sb.append("Borrow Count: ").append(item.getBorrowCount()).append("\n");
        sb.append("\n").append(item.getSummary());

        if (item instanceof Borrowable && !item.isAvailable()) {
            Borrowable b = (Borrowable) item;
            sb.append("\nBorrowed by: ").append(b.getBorrowedBy());
            sb.append("\nDue Date: ").append(b.getDueDate());
        }

        JOptionPane.showMessageDialog(parent, sb.toString(), "Item Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class ItemTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String available = (String) table.getValueAt(row, 5);
                if ("No".equals(available)) {
                    c.setBackground(new Color(255, 230, 230));
                } else {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 255));
                }
            }
            return c;
        }
    }
}
