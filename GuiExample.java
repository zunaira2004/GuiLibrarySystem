import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.Color;
import java.util.Scanner;

public class GuiExample extends JFrame{

    private JTable table;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton popularityButton;
    private int buttonReadCount;

    public GuiExample() {
        super("GUI Example");

        buttonReadCount=0;
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Title", "Author", "Publication Year", "Read Item"}, 0);

        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JButton readbutton=new JButton("Read");
                String[] data = line.split(",");
                tableModel.addRow(new Object[]{data[0], data[1], data[2], readbutton});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        table = new JTable(tableModel);

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        popularityButton=new JButton("Popularity count");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddItemDialog();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDeleteItemDialog();
            }
        });
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditItemDialog();
            }
        });
        popularityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(popularityButton);

        // Create the main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog(this, "Add Item");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();

        JLabel publicationYearLabel = new JLabel("Publication Year:");
        JTextField publicationYearField = new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String author = authorField.getText();
                String publicationYear = publicationYearField.getText();

                // Add the item to the table model
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(new Object[]{title, author, publicationYear, "read"});
                dialog.dispose();

                try {
                    BufferedWriter writer=new BufferedWriter(new FileWriter("books.txt",true));
                    writer.newLine();
                    writer.write(titleField.getText()+","+authorField.getText()+","+publicationYearField.getText()+",read");
                    writer.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                File file = new File(title + ".txt");

                if (file.exists()) {
                    file.delete();
                }

                FileWriter writer = null;
                try {
                    writer = new FileWriter(file);
                    writer.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        );


        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(authorLabel);
        panel.add(authorField);
        panel.add(publicationYearLabel);
        panel.add(publicationYearField);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(addButton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setVisible(true);

    }


    private void showDeleteItemDialog() {
        JDialog dialog = new JDialog(this, "Delete Item");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();

                // Delete the item from the table model
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (title.equals(tableModel.getValueAt(i, 0))) {
                        tableModel.removeRow(i);
                        break;
                    }
                }

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter("books.txt", false));

                    writer.write("");
                    writer.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                for (int i = 0; i < table.getRowCount(); i++) {
                    String t= (String) tableModel.getValueAt(i,0);
                    String A= (String) tableModel.getValueAt(i,1);
                    String P= (String) tableModel.getValueAt(i,2);

                    try {
                        writer = new BufferedWriter(new FileWriter("books.txt", true));
                        writer.write(t+","+A+","+P);
                        writer.newLine();
                        writer.close();
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                File file = new File(title + ".txt");

                if (file.exists()) {
                    file.delete();
                }
                dialog.dispose();
            }
        });

        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(new JPanel()); // Filler panel
        panel.add(deleteButton);

        dialog.add(panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void showEditItemDialog() {
        JDialog dialog = new JDialog(this, "Edit Item");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();

                // Find the item to be edited
                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                int row = -1;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (title.equals(tableModel.getValueAt(i, 0))) {
                        row = i;
                        break;
                    }
                }

                if (row >= 0) {
                    showEditItemDetailsDialog(tableModel, row);
                }

                dialog.dispose();
            }
        });

        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(new JPanel()); // Filler panel
        panel.add(editButton);

        dialog.add(panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void showEditItemDetailsDialog(DefaultTableModel tableModel, int row) {
        JDialog dialog = new JDialog(this, "Edit Item Details");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField((String) tableModel.getValueAt(row, 0));

        String oldTitle= (String) tableModel.getValueAt(row,0);

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField((String) tableModel.getValueAt(row, 1));

        JLabel publicationYearLabel = new JLabel("Publication Year:");
        JTextField publicationYearField = new JTextField((String) tableModel.getValueAt(row, 2));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title=titleField.getText();
                String author = authorField.getText();
                String publicationYear = publicationYearField.getText();

                if (!title.isEmpty()) {
                    tableModel.setValueAt(title, row, 0);
                }
                if (!author.isEmpty()) {
                    tableModel.setValueAt(author, row, 1);
                }
                if (!publicationYear.isEmpty()) {
                    tableModel.setValueAt(publicationYear, row, 2);
                }

                BufferedWriter writer=null;
                try {
                    writer = new BufferedWriter(new FileWriter("books.txt", false));

                    writer.write("");
                    writer.close();
                }
                catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                for (int i = 0; i < table.getRowCount(); i++)
                {
                    String t= (String) tableModel.getValueAt(i,0);
                    String A= (String) tableModel.getValueAt(i,1);
                    String P= (String) tableModel.getValueAt(i,2);

                    try {
                        writer = new BufferedWriter(new FileWriter("books.txt", true));
                        writer.write(t+","+A+","+P);
                        if(i-1!=table.getRowCount())
                              writer.newLine();
                        writer.close();
                    }
                    catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                File file = new File(oldTitle + ".txt");

                if (file.exists()) {
                    file.renameTo(new File(title + ".txt"));
                }


                dialog.dispose();
            }
        });

        panel.add(titleLabel);
        panel.add(titleField);
        panel.add(authorLabel);
        panel.add(authorField);
        panel.add(publicationYearLabel);
        panel.add(publicationYearField);
        panel.add(new JPanel()); // Filler panel
        panel.add(editButton);

        dialog.add(panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new GuiExample();
    }

}