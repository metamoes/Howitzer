import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.concurrent.Flow;

import javax.swing.table.DefaultTableModel;

public class ViewCVE extends JPanel {

    private Connection conn;
    private JTextField searchInput;
    private JButton searchButton;
    private String searchColumn;
    private JPanel mainPanel;
    private JCheckBox checkCVE;
    private JCheckBox checkPortlist;
    private JCheckBox checkExploits;

    class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
        WordWrapCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString()); // Convert value to String before setting the text
            setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            return this;
        }
    }
    public ViewCVE() {
        mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("ViewCVE");
        mainPanel.add(label);
    }

    public ViewCVE(Connection conn) {
        mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("ViewCVE");
        mainPanel.add(label);
        this.conn = conn;
        setupGUI();
    }
    
    private void setupGUI() {
        JButton viewButton = new JButton("View Data");
        checkCVE = new JCheckBox("CVE");
        checkPortlist = new JCheckBox("Portlist");
        checkExploits = new JCheckBox("Exploits");
        JPanel centerPanel = new JPanel();
        JPanel viewPanel = new JPanel();
        JPanel searchPanel = new JPanel();
        JPanel namePanel = new JPanel();
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel(); //put tables in here
        JLabel nameLabel = new JLabel("Database Name");

        topPanel.setLayout(new GridLayout(1,2));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        viewPanel.setLayout(new FlowLayout());
        centerPanel.setLayout(new BorderLayout());

        viewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (checkCVE.isSelected()) {
                    viewCVEData();
                }
                if (checkPortlist.isSelected()) {
                    viewPortlistData();
                }
                if (checkExploits.isSelected()) {
                    viewExploitsData();
                }
            }
        });
        
        searchInput = new JTextField(32);
        searchButton = new JButton("Search Database");
        
        searchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String searchText = searchInput.getText();
               if (checkCVE.isSelected()) {
                   searchColumn = "Description";
                   searchDatabase(searchText, "CVE");
               }
               if (checkPortlist.isSelected()) {
                   searchColumn = "Description";
                   searchDatabase(searchText, "Portlist");
               }
               if (checkExploits.isSelected()) {
                   searchColumn = "Description";
                   searchDatabase(searchText, "Exploits");
               }
           } 
        });

        mainPanel.add(viewPanel);
        viewPanel.add(checkCVE);
        viewPanel.add(checkPortlist);
        viewPanel.add(checkExploits);
        viewPanel.add(viewButton);
        mainPanel.add(centerPanel);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(bottomPanel, BorderLayout.CENTER);
        topPanel.add(namePanel);
        topPanel.add(searchPanel);
        searchPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        namePanel.setBorder(BorderFactory.createLineBorder(Color.black));
        searchPanel.add(searchButton);
        searchPanel.add(searchInput);
        namePanel.add(nameLabel);
    }
    
    private void viewCVEData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM CVE");
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("CVE Number");
            model.addColumn("Status");
            model.addColumn("Description");
            model.addColumn("References");
            
            while (rs.next()) {
                String[] row = {
                    rs.getString("CVE Number"),
                    rs.getString("Status"),
                    rs.getString("Description"),
                    rs.getString("References")
                };
                model.addRow(row);
            }

            JTable table = new JTable(model);
            table.setRowHeight(32); // Increase row height
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setCellRenderer(new WordWrapCellRenderer());
                tableColumn.setResizable(true);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            
            JOptionPane.showMessageDialog(this, scrollPane, "CVE Data", JOptionPane.PLAIN_MESSAGE);
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void viewPortlistData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM Portlist");
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Port");
            model.addColumn("Protocol");
            model.addColumn("Service_Name");
            model.addColumn("Description");
            
            while (rs.next()) {
                String[] row = {
                    rs.getString("Port"),
                    rs.getString("Protocol"),
                    rs.getString("Service_Name"),
                    rs.getString("Description")
                };
                model.addRow(row);
            }

            JTable table = new JTable(model);
            table.setRowHeight(32); // Increase row height
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setCellRenderer(new WordWrapCellRenderer());
                tableColumn.setResizable(true);
            }

            JScrollPane scrollPane = new JScrollPane(table);

            JOptionPane.showMessageDialog(this, scrollPane, "Portlist Data", JOptionPane.PLAIN_MESSAGE);
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void viewExploitsData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM Exploits");

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("File");
            model.addColumn("Description");
            model.addColumn("Author");
            model.addColumn("Type");
            model.addColumn("Platform");
            model.addColumn("Port");
            model.addColumn("Verified");
            model.addColumn("code_references");
            model.addColumn("tags");
            model.addColumn("aliases");
            model.addColumn("screenshot_url");
            model.addColumn("application_url");
            model.addColumn("Source_url");

            while (rs.next()) {
                String description = rs.getString("Description");
                JButton linkButton = new JButton("Search on exploit-db.com");
                linkButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        searchExploitDB(description);
                    }
                });

                String[] row = {
                        rs.getString("ID"),
                        rs.getString("File"),
                        description,
                        rs.getString("Author"),
                        rs.getString("Type"),
                        rs.getString("Platform"),
                        rs.getString("Port"),
                        rs.getString("Verified"),
                        rs.getString("code_references"),
                        rs.getString("tags"),
                        rs.getString("aliases"),
                        rs.getString("screenshot_url"),
                        rs.getString("application_url"),
                        rs.getString("Source_url")
                };
                model.addRow(row);
            }

            JTable table = new JTable(model);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRowHeight(32); // Increase row height
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setCellRenderer(new WordWrapCellRenderer());
                tableColumn.setResizable(true);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 600)); // Set a larger preferred size
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            JOptionPane.showMessageDialog(this, scrollPane, "Exploit Data", JOptionPane.PLAIN_MESSAGE);

            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchDatabase(String searchText, String tableName) {
        try {
            Statement search = conn.createStatement();
            ResultSet rs = search.executeQuery("SELECT * FROM " + tableName);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnTypeName = metaData.getColumnTypeName(i);
                if (columnTypeName.equals("VARCHAR") || columnTypeName.equals("CHAR") || columnTypeName.equals("TEXT") || columnTypeName.equals("INT") || columnTypeName.equals("DECIMAL") || columnTypeName.equals("DATE") || columnTypeName.equals("DATETIME") || columnTypeName.equals("TIME")) {
                    queryBuilder.append("CAST(`").append(columnName).append("` AS CHAR) LIKE '%").append(searchText).append("%'");
                } else {
                    queryBuilder.append("`").append(columnName).append("` LIKE '%").append(searchText).append("%'");
                }
                if (i != columnCount) {
                    queryBuilder.append(" OR ");
                }
            }

            rs = search.executeQuery(queryBuilder.toString());

            DefaultTableModel model = new DefaultTableModel();
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                model.addRow(row);
            }

            JTable table = new JTable(model);
            table.setRowHeight(32); // Increase row height
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableColumn tableColumn = table.getColumnModel().getColumn(column);
                tableColumn.setCellRenderer(new WordWrapCellRenderer());
                tableColumn.setResizable(true);
            }

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 600)); // Set a larger preferred size
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            JOptionPane.showMessageDialog(this, scrollPane, tableName + " Data", JOptionPane.PLAIN_MESSAGE);

            search.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchExploitDB(String description) {
        String search = "https://www.exploit-db.com/search?description=" + description.replaceAll(" ", "%20");
        try {
            Desktop.getDesktop().browse(new URI(search));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error opening ExploitDB.");
        }
    }
}
