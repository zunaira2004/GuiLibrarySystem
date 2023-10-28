import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class LibrarySystem extends JFrame{

    private static JTable table;
    private JButton addButton;
    private JButton deleteButton;
    private JButton editButton;
    private JButton popularityButton;
    private static DefaultTableModel tableModel;
    ArrayList<Integer> popularityCount=new ArrayList<Integer>();

    public LibrarySystem() {
        super("GUI Example");

        tableModel = new DefaultTableModel(new String[]{"Title", "Author", "Publication Year", "Read Item"}, 0);

        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                tableModel.addRow(new Object[]{data[0], data[1], data[2]});
                popularityCount.add(Integer.valueOf(data[3].trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        table = new JTable(tableModel);

        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        editButton = new JButton("Edit");
        popularityButton=new JButton("Popularity count");

        table.getColumnModel().getColumn(3).setCellRenderer(new RenderButtonForTable());
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditorForTable(new JTextField()));


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
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                int row = table.rowAtPoint(e.getPoint());
                if (row != 0)
                {
                    table.getSelectionModel().setSelectionInterval(row, row);
                    table.setSelectionBackground(Color.green);
                }
                else
                {
                    table.getSelectionModel().clearSelection();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(popularityButton);

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
        panel.setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField();

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField();

        JLabel publicationYearLabel = new JLabel("Publication Year:");
        JTextField publicationYearField = new JTextField();

        JLabel popularitylabel=new JLabel("Popularity count");
        JTextField popularityField=new JTextField();

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String author = authorField.getText();
                String publicationYear = publicationYearField.getText();
                int popularity= Integer.parseInt(popularityField.getText());
                popularityCount.add(popularity);
                display();

                DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
                tableModel.addRow(new Object[]{title, author, publicationYear});
                dialog.dispose();

                try {
                    BufferedWriter writer=new BufferedWriter(new FileWriter("books.txt",true));
                    writer.newLine();
                    writer.write(titleField.getText()+","+authorField.getText()+","+publicationYearField.getText()+","+popularityField.getText());
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
        panel.add(popularitylabel);
        panel.add(popularityField);

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
                        popularityCount.remove(i);
                        break;
                    }
                }
                display();

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
                        writer.write(t+","+A+","+P+","+popularityCount.get(i));
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
        panel.add(new JPanel());
        panel.add(editButton);

        dialog.add(panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }

    private void showEditItemDetailsDialog(DefaultTableModel tableModel, int row) {
        JDialog dialog = new JDialog(this, "Edit Item Details");
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel titleLabel = new JLabel("Title:");
        JTextField titleField = new JTextField((String) tableModel.getValueAt(row, 0));

        String oldTitle= (String) tableModel.getValueAt(row,0);

        JLabel authorLabel = new JLabel("Author:");
        JTextField authorField = new JTextField((String) tableModel.getValueAt(row, 1));

        JLabel publicationYearLabel = new JLabel("Publication Year:");
        JTextField publicationYearField = new JTextField((String) tableModel.getValueAt(row, 2));

        JLabel popularityLabel=new JLabel("Popularity Count:");
        JTextField popularityField=new JTextField((int) popularityCount.get(row));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popularityCount.remove(row);
                popularityCount.add(row, Integer.valueOf(popularityField.getText()));
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
                        writer.write(t+","+A+","+P+","+popularityCount.get(i));
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
        panel.add(popularityLabel);
        panel.add(popularityField);
        panel.add(new JPanel()); // Filler panel
        panel.add(editButton);

        dialog.add(panel, BorderLayout.CENTER);

        dialog.pack();
        dialog.setVisible(true);
    }
    public void display()
    {
        for(int i=0;i<popularityCount.size();i++)
        {
            System.out.println(popularityCount.get(i)+" ");
        }
    }
    public static DefaultTableModel getTableModel() {
        return tableModel;
    }

    public static JTable getTable() {
        return table;
    }


    public static void main(String[] args) {
        new LibrarySystem();
    }

}

class RenderButtonForTable extends JButton implements TableCellRenderer
{
    public RenderButtonForTable()
    {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText("read");
        return this;
    }
}

class ButtonEditorForTable extends DefaultCellEditor
{
    private final JButton button;
    private String label;
    private boolean isPushed;
    private DefaultTableModel tableModel;
    private JTable table;

    public ButtonEditorForTable(JTextField textField) {
        super(textField);
        button = new JButton();
        button.setOpaque(true);
        setTable();
        setTableModel();
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.convertRowIndexToModel(table.getEditingRow());
                String selectedItemTitle = tableModel.getValueAt(selectedRow, 0).toString();

                JFrame readingFrame = new JFrame(selectedItemTitle);
                readingFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JTextArea textArea = new JTextArea(20, 40);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                JScrollPane scrollPane = new JScrollPane(textArea);

                try (BufferedReader reader = new BufferedReader(new FileReader(selectedItemTitle + ".txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line + "\n");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                readingFrame.add(scrollPane);
                readingFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(readingFrame, "Are you sure you want to stop reading this item?", "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            readingFrame.dispose();
                        }
                        else {
                            readingFrame.setDefaultCloseOperation(readingFrame.DO_NOTHING_ON_CLOSE);
                        }
                    }
                });

                readingFrame.pack();
                readingFrame.setVisible(true);
            }
        });
    }
    public void setTableModel() {
        this.tableModel = LibrarySystem.getTableModel();
    }

    public void setTable() {
        this.table = LibrarySystem.getTable();
    }


    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected)
        {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else
        {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue()
    {
        if (isPushed)
        {

        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing()
    {
        isPushed = false;
        return super.stopCellEditing();
    }
}