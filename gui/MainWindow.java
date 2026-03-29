package gui;

import controller.LibraryManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame {
    private LibraryManager manager;
    private JTabbedPane tabbedPane;
    private JLabel statusBar;
    private ViewItemsPanel viewItemsPanel;
    private BorrowPanel borrowPanel;
    private AdminPanel adminPanel;
    private SearchSortPanel searchSortPanel;
    private Timer overdueTimer;

    public MainWindow() {
        manager = new LibraryManager();
        setTitle("Smart Library Circulation & Automation System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        buildMenuBar();
        buildTabs();
        buildStatusBar();
        setupOverdueTimer();
        setupWindowClosing();

        setVisible(true);
        setStatus("System loaded. " + manager.getDb().getItems().size() + " items in library.");
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem saveItem = new JMenuItem("Save Data", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> {
            manager.saveData();
            setStatus("Data saved successfully.");
        });
        JMenuItem reportItem = new JMenuItem("Generate Report");
        reportItem.addActionListener(e -> showReport());
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmAndExit());
        fileMenu.add(saveItem);
        fileMenu.add(reportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        JMenuItem undoItem = new JMenuItem("Undo Last Admin Action");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        undoItem.addActionListener(e -> {
            String result = manager.undoLastAction();
            setStatus(result);
            refreshAllPanels();
        });
        editMenu.add(undoItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Smart Library Circulation & Automation System\nCOS 202 - MIVA Open University",
            "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    private void buildTabs() {
        tabbedPane = new JTabbedPane();

        viewItemsPanel = new ViewItemsPanel(manager, this);
        borrowPanel = new BorrowPanel(manager, this);
        adminPanel = new AdminPanel(manager, this);
        searchSortPanel = new SearchSortPanel(manager, this);

        tabbedPane.addTab("View Items", new ImageIcon(), viewItemsPanel, "Browse all library items");
        tabbedPane.addTab("Borrow / Return", new ImageIcon(), borrowPanel, "Borrow or return items");
        tabbedPane.addTab("Admin", new ImageIcon(), adminPanel, "Manage items and users");
        tabbedPane.addTab("Search & Sort", new ImageIcon(), searchSortPanel, "Search and sort items");

        tabbedPane.addChangeListener(e -> refreshAllPanels());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void buildStatusBar() {
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void setupOverdueTimer() {
        overdueTimer = new Timer(30000, e -> checkOverdue());
        overdueTimer.start();
    }

    private void checkOverdue() {
        int count = manager.getBorrowController().getOverdueItems().size();
        if (count > 0) {
            setStatus("WARNING: " + count + " overdue item(s) detected.");
        }
    }

    private void setupWindowClosing() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });
    }

    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Save data before exiting?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            manager.saveData();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    private void showReport() {
        String report = manager.generateReport();
        JTextArea area = new JTextArea(report);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 350));

        int choice = JOptionPane.showOptionDialog(this, scroll, "Library Report",
            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            new String[]{"Export to File", "Close"}, "Close");

        if (choice == 0) {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new java.io.File("library_report.txt"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                utils.FileHandler.exportReport(report, chooser.getSelectedFile().getName());
                setStatus("Report exported.");
            }
        }
    }

    public void setStatus(String message) {
        statusBar.setText(message);
    }

    public void refreshAllPanels() {
        viewItemsPanel.refresh();
        borrowPanel.refresh();
        adminPanel.refresh();
        searchSortPanel.refresh();
    }
}
