package gui;

import controller.*;
import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SearchSortPanel extends JPanel {
    private LibraryManager manager;
    private MainWindow parent;

    private JTextField searchField;
    private JComboBox<String> searchFieldCombo;
    private JComboBox<String> searchAlgoCombo;
    private JComboBox<String> sortFieldCombo;
    private JComboBox<String> sortAlgoCombo;
    private JTable resultTable;
    private DefaultTableModel resultModel;
    private JLabel resultCountLabel;

    public SearchSortPanel(LibraryManager manager, MainWindow parent) {
        this.manager = manager;
        this.parent = parent;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buildUI();
        refresh();
    }

    private void buildUI() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        searchPanel.add(new JLabel("Query:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        searchField = new JTextField();
        searchField.setToolTipText("Enter search query");
        searchField.addActionListener(e -> handleSearch());
        searchPanel.add(searchField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        searchPanel.add(new JLabel("Search by:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        searchFieldCombo = new JComboBox<>(new String[]{"Title", "Author", "Type"});
        searchPanel.add(searchFieldCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        searchPanel.add(new JLabel("Algorithm:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        searchAlgoCombo = new JComboBox<>(new String[]{"Linear Search", "Binary Search", "Recursive Search"});
        searchAlgoCombo.setToolTipText("Binary search requires title field and a sorted list");
        searchPanel.add(searchAlgoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> handleSearch());
        searchPanel.add(searchBtn, gbc);

        JPanel sortPanel = new JPanel(new GridBagLayout());
        sortPanel.setBorder(BorderFactory.createTitledBorder("Sort"));
        GridBagConstraints sgbc = new GridBagConstraints();
        sgbc.insets = new Insets(5, 5, 5, 5);
        sgbc.fill = GridBagConstraints.HORIZONTAL;
        sgbc.anchor = GridBagConstraints.WEST;

        sgbc.gridx = 0; sgbc.gridy = 0; sgbc.weightx = 0;
        sortPanel.add(new JLabel("Sort by:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1;
        sortFieldCombo = new JComboBox<>(new String[]{"Title", "Author", "Year"});
        sortPanel.add(sortFieldCombo, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 1; sgbc.weightx = 0;
        sortPanel.add(new JLabel("Algorithm:"), sgbc);
        sgbc.gridx = 1; sgbc.weightx = 1;
        sortAlgoCombo = new JComboBox<>(new String[]{"Selection Sort", "Insertion Sort", "Merge Sort", "Quick Sort"});
        sortPanel.add(sortAlgoCombo, sgbc);

        sgbc.gridx = 0; sgbc.gridy = 2; sgbc.gridwidth = 2;
        JButton sortBtn = new JButton("Sort All Items");
        sortBtn.addActionListener(e -> handleSort());
        sortPanel.add(sortBtn, sgbc);

        sgbc.gridy = 3;
        JButton resetBtn = new JButton("Reset (Show All)");
        resetBtn.addActionListener(e -> refresh());
        sortPanel.add(resetBtn, sgbc);

        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.insets = new Insets(5, 5, 5, 5);
        tgbc.fill = GridBagConstraints.BOTH;
        tgbc.weightx = 1; tgbc.weighty = 1;
        tgbc.gridx = 0; tgbc.gridy = 0;
        topPanel.add(searchPanel, tgbc);
        tgbc.gridx = 1;
        topPanel.add(sortPanel, tgbc);

        String[] cols = {"ID", "Type", "Title", "Author", "Year", "Available"};
        resultModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(resultModel);
        resultTable.setRowHeight(24);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        resultCountLabel = new JLabel("Results: 0");
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomBar.add(resultCountLabel);

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Results"));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { showError("Enter a search query."); return; }

        String field = ((String) searchFieldCombo.getSelectedItem()).toLowerCase();
        String algo = (String) searchAlgoCombo.getSelectedItem();
        List<LibraryItem> results = new ArrayList<>();
        SearchEngine se = manager.getSearchEngine();
        List<LibraryItem> items = manager.getDb().getItems();

        try {
            switch (algo) {
                case "Linear Search":
                    results = se.linearSearch(items, query, field);
                    break;
                case "Binary Search":
                    List<LibraryItem> sorted = manager.sortItems(
                        SortEngine.SortField.TITLE, SortEngine.SortAlgorithm.MERGE);
                    LibraryItem found = se.binarySearchByTitle(sorted, query);
                    if (found != null) results.add(found);
                    break;
                case "Recursive Search":
                    if ("author".equals(field)) {
                        results = se.recursiveSearchByAuthor(items, query, 0, new ArrayList<>());
                    } else {
                        LibraryItem r = se.recursiveSearchByTitle(items, query, 0);
                        if (r != null) results.add(r);
                    }
                    break;
            }
        } catch (Exception ex) {
            showError("Search error: " + ex.getMessage());
            return;
        }

        populateTable(results);
        parent.setStatus("Search: '" + query + "' using " + algo + " — " + results.size() + " result(s).");
    }

    private void handleSort() {
        String field = (String) sortFieldCombo.getSelectedItem();
        String algo = (String) sortAlgoCombo.getSelectedItem();

        SortEngine.SortField sf = SortEngine.SortField.valueOf(field.toUpperCase());
        SortEngine.SortAlgorithm sa;
        switch (algo) {
            case "Insertion Sort": sa = SortEngine.SortAlgorithm.INSERTION; break;
            case "Merge Sort":     sa = SortEngine.SortAlgorithm.MERGE; break;
            case "Quick Sort":     sa = SortEngine.SortAlgorithm.QUICK; break;
            default:               sa = SortEngine.SortAlgorithm.SELECTION;
        }

        List<LibraryItem> sorted = manager.sortItems(sf, sa);
        populateTable(sorted);
        parent.setStatus("Sorted by " + field + " using " + algo + ".");
    }

    private void populateTable(List<LibraryItem> items) {
        resultModel.setRowCount(0);
        for (LibraryItem item : items) {
            resultModel.addRow(new Object[]{
                item.getId(), item.getType(), item.getTitle(),
                item.getAuthor(), item.getYear(),
                item.isAvailable() ? "Yes" : "No"
            });
        }
        resultCountLabel.setText("Results: " + items.size());
    }

    public void refresh() {
        populateTable(manager.getDb().getItems());
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
