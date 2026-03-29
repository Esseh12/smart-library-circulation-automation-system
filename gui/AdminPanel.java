package gui;

import controller.LibraryManager;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JTable usersTable;
    private DefaultTableModel usersModel;
    private JTextField nameField, emailField;

    private JTextField titleField, authorField, yearField;
    private JTextField extra1Field, extra2Field;
    private JLabel extra1Label, extra2Label;
    private JComboBox<String> itemTypeCombo;

    public AdminPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel switchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showItemsBtn = new JButton("Manage Items");
        JButton showUsersBtn = new JButton("Manage Users");
        JButton undoBtn = new JButton("Undo Last Action");
        undoBtn.setToolTipText("Undo the last add or delete operation");
        showItemsBtn.addActionListener(e -> cardLayout.show(cardPanel, "ITEMS"));
        showUsersBtn.addActionListener(e -> cardLayout.show(cardPanel, "USERS"));
        undoBtn.addActionListener(e -> {
            String result = manager.undoLastAction();
            JOptionPane.showMessageDialog(parent, result, "Undo", JOptionPane.INFORMATION_MESSAGE);
            parent.refreshAllPanels();
        });
        switchBar.add(showItemsBtn);
        switchBar.add(showUsersBtn);
        switchBar.add(Box.createHorizontalStrut(20));
        switchBar.add(undoBtn);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(buildItemsPanel(), "ITEMS");
        cardPanel.add(buildUsersPanel(), "USERS");

        add(switchBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);
    }

    private JPanel buildItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Item"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        itemTypeCombo = new JComboBox<>(new String[]{"Book", "Magazine", "Journal"});
        itemTypeCombo.addActionListener(e -> updateExtraLabels());
        form.add(itemTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        titleField = new JTextField();
        form.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        authorField = new JTextField();
        form.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        yearField = new JTextField();
        form.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        extra1Label = new JLabel("ISBN:");
        form.add(extra1Label, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        extra1Field = new JTextField();
        form.add(extra1Field, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        extra2Label = new JLabel("Genre:");
        form.add(extra2Label, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        extra2Field = new JTextField();
        form.add(extra2Field, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Item");
        JButton clearBtn = new JButton("Clear");
        addBtn.addActionListener(e -> handleAddItem());
        clearBtn.addActionListener(e -> clearItemForm());
        btnRow.add(addBtn);
        btnRow.add(clearBtn);
        form.add(btnRow, gbc);

        String[] cols = {"ID", "Type", "Title", "Author", "Year"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable itemList = new JTable(model) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row >= 0) {
                    String id = (String) model.getValueAt(row, 0);
                    LibraryItem item = manager.getDb().findById(id);
                    return item != null ? item.getSummary() : null;
                }
                return null;
            }
        };
        itemList.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(itemList);
        scroll.setBorder(BorderFactory.createTitledBorder("All Items"));

        JPanel deleteRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField deleteIdField = new JTextField(10);
        deleteIdField.setToolTipText("Enter Item ID to delete");
        JButton deleteBtn = new JButton("Delete Item");
        deleteBtn.addActionListener(e -> {
            String id = deleteIdField.getText().trim();
            if (id.isEmpty()) {
                int row = itemList.getSelectedRow();
                if (row >= 0) id = (String) model.getValueAt(row, 0);
            }
            if (id.isEmpty()) { showError("Enter or select an Item ID."); return; }
            int confirm = JOptionPane.showConfirmDialog(parent,
                "Delete item " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean ok = manager.deleteItem(id);
                parent.setStatus(ok ? "Deleted: " + id : "Item not found: " + id);
                refreshItemTable(model);
                parent.refreshAllPanels();
            }
        });
        deleteRow.add(new JLabel("Delete ID:"));
        deleteRow.add(deleteIdField);
        deleteRow.add(deleteBtn);

        JPanel right = new JPanel(new BorderLayout(4, 4));
        right.add(scroll, BorderLayout.CENTER);
        right.add(deleteRow, BorderLayout.SOUTH);

        itemList.getSelectionModel().addListSelectionListener(ev -> {
            int row = itemList.getSelectedRow();
            if (row >= 0) deleteIdField.setText((String) model.getValueAt(row, 0));
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
        split.setResizeWeight(0.35);
        panel.add(split, BorderLayout.CENTER);

        panel.putClientProperty("itemTableModel", model);
        return panel;
    }

    private JPanel buildUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New User"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        nameField = new JTextField();
        form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        emailField = new JTextField();
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JButton addUserBtn = new JButton("Add User");
        addUserBtn.addActionListener(e -> handleAddUser());
        form.add(addUserBtn, gbc);

        String[] cols = {"User ID", "Name", "Email", "Current Borrows"};
        usersModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        usersTable = new JTable(usersModel);
        usersTable.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(usersTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Registered Users"));

        JPanel deleteRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField deleteIdField = new JTextField(10);
        JButton deleteBtn = new JButton("Delete User");
        deleteBtn.addActionListener(e -> {
            String id = deleteIdField.getText().trim();
            if (id.isEmpty()) {
                int row = usersTable.getSelectedRow();
                if (row >= 0) id = (String) usersModel.getValueAt(row, 0);
            }
            if (id.isEmpty()) { showError("Enter or select a User ID."); return; }
            boolean ok = manager.deleteUser(id);
            parent.setStatus(ok ? "Deleted user: " + id : "User not found: " + id);
            refresh();
        });
        deleteRow.add(new JLabel("Delete User ID:"));
        deleteRow.add(deleteIdField);
        deleteRow.add(deleteBtn);

        usersTable.getSelectionModel().addListSelectionListener(e -> {
            int row = usersTable.getSelectedRow();
            if (row >= 0) deleteIdField.setText((String) usersModel.getValueAt(row, 0));
        });

        JPanel right = new JPanel(new BorderLayout(4, 4));
        right.add(scroll, BorderLayout.CENTER);
        right.add(deleteRow, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
        split.setResizeWeight(0.35);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private void handleAddItem() {
        try {
            String type = (String) itemTypeCombo.getSelectedItem();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String yearStr = yearField.getText().trim();
            String extra1 = extra1Field.getText().trim();
            String extra2 = extra2Field.getText().trim();

            if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty()) {
                showError("Title, Author, and Year are required.");
                return;
            }
            int year = Integer.parseInt(yearStr);
            switch (type) {
                case "Book": manager.addBook(title, author, year, extra1, extra2); break;
                case "Magazine": manager.addMagazine(title, author, year, extra1, extra2); break;
                case "Journal": manager.addJournal(title, author, year, extra1, extra2); break;
            }
            clearItemForm();
            parent.setStatus("Added: " + title);
            parent.refreshAllPanels();
        } catch (NumberFormatException ex) {
            showError("Year must be a valid number.");
        }
    }

    private void handleAddUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        if (name.isEmpty() || email.isEmpty()) { showError("Name and Email are required."); return; }
        manager.addUser(name, email);
        nameField.setText("");
        emailField.setText("");
        parent.setStatus("User added: " + name);
        refresh();
    }

    private void clearItemForm() {
        titleField.setText(""); authorField.setText("");
        yearField.setText(""); extra1Field.setText(""); extra2Field.setText("");
    }

    private void updateExtraLabels() {
        String type = (String) itemTypeCombo.getSelectedItem();
        switch (type) {
            case "Book": extra1Label.setText("ISBN:"); extra2Label.setText("Genre:"); break;
            case "Magazine": extra1Label.setText("Issue No:"); extra2Label.setText("Publisher:"); break;
            case "Journal": extra1Label.setText("Volume:"); extra2Label.setText("Field:"); break;
        }
    }

    private void refreshItemTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (LibraryItem item : manager.getDb().getItems()) {
            model.addRow(new Object[]{item.getId(), item.getType(), item.getTitle(), item.getAuthor(), item.getYear()});
        }
    }

    public void refresh() {
        usersModel.setRowCount(0);
        for (UserAccount user : manager.getDb().getUsers()) {
            usersModel.addRow(new Object[]{
                user.getUserId(), user.getName(), user.getEmail(), user.getCurrentBorrows().size()
            });
        }

        Component itemsCard = ((JPanel) cardPanel.getComponent(0));
        Object model = itemsCard.getClass() == JPanel.class ?
            ((JPanel) itemsCard).getClientProperty("itemTableModel") : null;
        if (model instanceof DefaultTableModel) refreshItemTable((DefaultTableModel) model);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
