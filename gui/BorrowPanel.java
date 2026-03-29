package gui;

import controller.LibraryManager;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BorrowPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;

    private JComboBox<UserAccount> userCombo;
    private JTextField itemIdField;
    private JTextArea resultArea;
    private JTable borrowedTable;
    private DefaultTableModel borrowedModel;
    private JTable overdueTable;
    private DefaultTableModel overdueModel;

    public BorrowPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Borrow / Return"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select User:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        userCombo = new JComboBox<>();
        userCombo.setToolTipText("Select a registered user");
        formPanel.add(userCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("Item ID:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        itemIdField = new JTextField();
        itemIdField.setToolTipText("Enter the Item ID to borrow or return");
        formPanel.add(itemIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton borrowBtn = new JButton("Borrow");
        JButton returnBtn = new JButton("Return");
        borrowBtn.setToolTipText("Borrow selected item for selected user");
        returnBtn.setToolTipText("Return selected item for selected user");
        borrowBtn.addActionListener(e -> handleBorrow());
        returnBtn.addActionListener(e -> handleReturn());
        btnPanel.add(borrowBtn);
        btnPanel.add(returnBtn);
        formPanel.add(btnPanel, gbc);

        gbc.gridy = 3;
        resultArea = new JTextArea(3, 40);
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(245, 255, 245));
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        formPanel.add(new JScrollPane(resultArea), gbc);

        String[] borrowedCols = {"Item ID", "Title", "Type", "Borrowed By", "Due Date"};
        borrowedModel = new DefaultTableModel(borrowedCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        borrowedTable = new JTable(borrowedModel);
        borrowedTable.setRowHeight(24);
        JScrollPane borrowedScroll = new JScrollPane(borrowedTable);
        borrowedScroll.setBorder(BorderFactory.createTitledBorder("Currently Borrowed Items"));

        String[] overdueCols = {"Item ID", "Title", "Borrowed By", "Due Date", "Fine (₦)"};
        overdueModel = new DefaultTableModel(overdueCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        overdueTable = new JTable(overdueModel);
        overdueTable.setRowHeight(24);
        overdueTable.setBackground(new Color(255, 235, 235));
        JScrollPane overdueScroll = new JScrollPane(overdueTable);
        overdueScroll.setBorder(BorderFactory.createTitledBorder("Overdue Items"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, borrowedScroll, overdueScroll);
        splitPane.setResizeWeight(0.6);

        add(formPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void handleBorrow() {
        UserAccount user = (UserAccount) userCombo.getSelectedItem();
        String itemId = itemIdField.getText().trim();
        if (user == null) { showError("Please select a user."); return; }
        if (itemId.isEmpty()) { showError("Please enter an Item ID."); return; }
        String result = manager.borrow(itemId, user.getUserId());
        resultArea.setText(result);
        parent.setStatus(result);
        refresh();
    }

    private void handleReturn() {
        UserAccount user = (UserAccount) userCombo.getSelectedItem();
        String itemId = itemIdField.getText().trim();
        if (user == null) { showError("Please select a user."); return; }
        if (itemId.isEmpty()) { showError("Please enter an Item ID."); return; }
        String result = manager.returnItem(itemId, user.getUserId());
        resultArea.setText(result);
        parent.setStatus(result);
        refresh();
    }

    public void refresh() {
        userCombo.removeAllItems();
        for (UserAccount user : manager.getDb().getUsers()) {
            userCombo.addItem(user);
        }

        borrowedModel.setRowCount(0);
        overdueModel.setRowCount(0);

        List<LibraryItem> overdue = manager.getBorrowController().getOverdueItems();

        for (LibraryItem item : manager.getDb().getItems()) {
            if (!item.isAvailable() && item instanceof Borrowable) {
                Borrowable b = (Borrowable) item;
                borrowedModel.addRow(new Object[]{
                    item.getId(), item.getTitle(), item.getType(),
                    b.getBorrowedBy(), b.getDueDate()
                });
            }
        }

        for (LibraryItem item : overdue) {
            Borrowable b = (Borrowable) item;
            double fine = manager.getBorrowController().computeOverdueFine(item);
            overdueModel.addRow(new Object[]{
                item.getId(), item.getTitle(), b.getBorrowedBy(), b.getDueDate(),
                String.format("%.2f", fine)
            });
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}
