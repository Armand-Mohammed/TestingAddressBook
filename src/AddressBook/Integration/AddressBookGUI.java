package AddressBook.Integration;//package GUI;


import AddressBook.Unit.AddressBook;
import AddressBook.Unit.AddressBookController;
import AddressBook.Unit.FileSystem;
import AddressBook.Unit.Person;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.regex.Pattern;

public class AddressBookGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    //Shows GUI
    private static void createAndShowGUI() {
        AddressBookGUI gui = new AddressBookGUI();
        gui.setVisible(true);
    }

    //Main Function required by JDC call createAndShowGUI()
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    //Organize properties for GUI and addressbook
    private final AddressBook addressBook = new AddressBook();
    private final AddressBookController controller = new AddressBookController(addressBook);
    private final JTable nameList = new JTable(addressBook);
    private final TableRowSorter<AddressBook> tableRowSorter = new TableRowSorter<>(addressBook);
    private final JButton addButton = new JButton("Add...");
    private final JButton editButton = new JButton("Edit...");
    private final JButton deleteButton = new JButton("Delete");
    private final JMenuItem newItem = new JMenuItem("New", 'N');
    private final JMenuItem openItem = new JMenuItem("Open", 'O');
    private final JMenuItem saveItem = new JMenuItem("Save", 'S');
    private final JMenuItem saveAsItem = new JMenuItem("Save As...", 'A');
    private final JMenuItem printItem = new JMenuItem("Print", 'P');
    private final JMenuItem quitItem = new JMenuItem("Exit", 'X');
    private final JTextField searchTextField = new JTextField("");

    private File currentFile = null;

    //AddressBook Method create GUI components and adds them to the GUI
    public AddressBookGUI() {
        // Arrange the window controls
        nameList.setRowSorter(tableRowSorter);
        nameList.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(nameList);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        //Set Names for each GUI components
        nameList.setName("table");
        addButton.setName("add");
        editButton.setName("edit");
        deleteButton.setName("delete");
        newItem.setName("new");
        openItem.setName("open");
        saveItem.setName("save");
        saveAsItem.setName("saveAs");
        printItem.setName("print");
        quitItem.setName("quit");
        searchTextField.setName("search");

        //Creates File drop down Menu and set action listener on File menu
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.setName("file");
        file.setMnemonic('F');
        //creates "new" dropdown item and action listener on GUI item
        newItem.addActionListener(e ->
        {
            if (saveItem.isEnabled() && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Are you sure you want to create a new address book? Any unsaved progress will be lost.", "New Address Book", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                return;
            }
            controller.clear();
            saveItem.setEnabled(false);
        });
        file.add(newItem);
        //End "new" component item
        //creates "open" dropdown item and action listener on GUI item
        openItem.addActionListener(e ->
        {
            if (saveItem.isEnabled() && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Are you sure you want to create a new address book? Any unsaved progress will be lost.", "New Address Book", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                return;
            }
            final JFileChooser jfc = new JFileChooser();
            if (JFileChooser.APPROVE_OPTION != jfc.showOpenDialog(this)) {
                return;
            }
            try {
                controller.open(jfc.getSelectedFile());
                currentFile = jfc.getSelectedFile();
                saveItem.setEnabled(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Open", JOptionPane.ERROR_MESSAGE);
            }
        });
        file.add(openItem);
        //end "open" component part
        //creates "save" dropdown item and action listener on GUI item
        saveItem.setEnabled(false);
        saveItem.addActionListener(e ->
        {
            if (currentFile == null) {
                saveAsItem.doClick();
                return;
            }
            FileSystem fs = new FileSystem();
            try {
                controller.save(currentFile);
                saveItem.setEnabled(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving the file: " + ex.getMessage(), "Save", JOptionPane.ERROR_MESSAGE);
            }
        });

        //"save" dropdown item and change listener on GUI item
        saveItem.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Set saveAsItem with the same state as saveItem
                saveAsItem.setEnabled(saveItem.isEnabled());
            }
        });
        saveAsItem.setEnabled(false);
        file.add(saveItem);
        //end 'Save" component

        //creates "saveAs" dropdown item and action listener on GUI item
        saveAsItem.addActionListener(e ->
        {
            final JFileChooser jfc = new JFileChooser();
            if (JFileChooser.APPROVE_OPTION != jfc.showSaveDialog(this)) {
                return;
            }
            currentFile = jfc.getSelectedFile();
            if (currentFile == null) {
                return;
            }
            if (currentFile.exists() && JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Are you sure you want to overwrite this file?", "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                return;
            }
            saveItem.doClick();
        });
        file.add(saveAsItem);
        file.add(new JSeparator());
        //end 'saveAs' component code
        //creates "print" dropdown item and action listener on GUI item
        printItem.addActionListener(e ->
        {
           try {
                nameList.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print", JOptionPane.WARNING_MESSAGE);
            }
        });
        file.add(printItem);
        //End 'Print' item component
        file.add(new JSeparator());
        //creates "quit" dropdown item and action listener on GUI item
        quitItem.addActionListener(al -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        file.add(quitItem);
        menuBar.add(file);
        menuBar.add(new JSeparator());
        //end 'quit' component
        //creates "Search" box component of GUI
        menuBar.add(new JLabel("Search: "));
        searchTextField.setMaximumSize(new Dimension(15000, 50));
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            // Listen to the Document so the list filters immediately
            // (EventListener on JTextField requires "Enter" before firing)
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            public void filter() {
                //Filter for finding patterns that match the search box
                tableRowSorter.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(searchTextField.getText())));
            }
        });
        menuBar.add(searchTextField);
        //End search component
        //Creates panel for Add, Edit, Delete JButtons
        JPanel addEditDelPanel = new JPanel();
        //Creates 'add' button component to the GUI
        addButton.setMnemonic('A');
        addButton.addActionListener(e ->
        {
            PersonDialog dialog = new PersonDialog(this);

            if (dialog.showDialog() != PersonDialog.Result.OK || dialog.getPerson() == null ) {
                return;
            }

            controller.add(dialog.getPerson());
            saveItem.setEnabled(true);
        });
        addEditDelPanel.add(addButton);
        //End 'add' Button component

        //Creates 'edit' button component
        editButton.setMnemonic('E');
        editButton.addActionListener(e -> {
            int selectedRow = nameList.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }

            // TODO: This doesn't work yet
            int index = nameList.convertRowIndexToModel(selectedRow);
            Person oldPerson = controller.get(index);
            PersonDialog dialog = new PersonDialog(this, oldPerson);

            if (dialog.showDialog() != PersonDialog.Result.OK) {
                return;
            }
            controller.set(index, dialog.getPerson());
            saveItem.setEnabled(true);
        });
        addEditDelPanel.add(editButton);
        //End 'edit' button component

        //Creates 'delete' button component
        deleteButton.setMnemonic('D');
        deleteButton.addActionListener(e -> {
            int selectedRow = nameList.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            controller.remove(nameList.convertRowIndexToModel(selectedRow));
            saveItem.setEnabled(true);
        });
        addEditDelPanel.add(deleteButton);
        //End 'delete' button component
        // Bottom area
        JPanel panelPanel = new JPanel(new BorderLayout());
        panelPanel.add(addEditDelPanel, BorderLayout.LINE_START);
        panelPanel.add(new JLabel("TIP: You can sort by clicking the column headers"), BorderLayout.LINE_END);
        getContentPane().add(panelPanel, BorderLayout.PAGE_END);

        // Set window parameters
        setSize(800, 600);
        setLocationByPlatform(true);

        //Sets GUI title
        setTitle("Address Book");
        //Sets the menubar on the GUI
        setJMenuBar(menuBar);
        //Creates window listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                JFrame frame = (JFrame) e.getSource();
                // We use saveItem.isEnabled to indicate whether there are unsaved changes or not
                if (!saveItem.isEnabled() || JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit? Your changes will be lost.", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }
}