import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewCVE extends JPanel {

    private Connection conn;
    private JTextField searchInput;
    private JButton searchButton;
    private String searchColumn;
    
    public ViewCVE() {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("ViewCVE");
        mainPanel.add(label);
    }

    public ViewCVE(Connection conn) {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("ViewCVE");
        mainPanel.add(label);
        this.conn = conn;
        setupGUI();
    }
    
    private void setupGUI() {
        JButton viewCVE = new JButton("View CVE Data");
        JButton viewPortlist = new JButton("View Portlist Data");
        JButton viewExploits = new JButton("View Exploits Data");
        
        searchInput = new JTextField(32);
        searchButton = new JButton("Search Database");
        
        searchButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String searchText = searchInput.getText();
               if (viewCVE.isSelected()) {
                   searchCVE(searchText);
               } else if (viewPortlist.isSelected()) {
                   searchPortlist(searchText);
               } else if (viewExploits.isSelected()) {
                   searchExploits(searchText);
               }
           } 
        });
    
        viewCVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCVEData();
            }
        });
        
        viewCVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchInput.getText();
                searchCVE(searchText);
            }
        });
        
        viewPortlist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPortlistData();
            }
        });
        
        viewPortlist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchInput.getText();
                searchPortlist(searchText);
            }
        });
        
        viewExploits.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewExploitsData();
            }
        });
        
        viewExploits.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String searchText = searchInput.getText();
                searchExploits(searchText);
            }
        });
        
        add(viewCVE);
        add(viewPortlist);
        add(viewExploits);
        add(searchButton);
        add(searchInput);
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
            JScrollPane scrollPane = new JScrollPane(table);
            
            JOptionPane.showMessageDialog(this, scrollPane, "CVE Data", JOptionPane.PLAIN_MESSAGE);
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchCVE(String searchText) {
        searchColumn = "CVE Number";
        searchDatabase(searchText, "CVE");
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
                    rs.getString("Portlist Number"),
                    rs.getString("Status"),
                    rs.getString("Description"),
                    rs.getString("References")
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            
            JOptionPane.showMessageDialog(this, scrollPane, "Portlist Data", JOptionPane.PLAIN_MESSAGE);
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchPortlist(String searchText) {
        searchColumn = "Port";
        searchDatabase(searchText, "Portlist");
    }
    
    private void viewExploitsData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM Exploits");
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("File");
            model.addColumn("Description");
            model.addColumn("date_published");
            model.addColumn("Author");
            model.addColumn("Type");
            model.addColumn("Platform");
            model.addColumn("Port");
            model.addColumn("Date_Added");
            model.addColumn("Date_Updated");
            model.addColumn("Verified");
            model.addColumn("Code_References");
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
                    rs.getString("Exploit Number"),
                    rs.getString("Status"),
                    description,
                    rs.getString("References")
                };
                model.addRow(row);
            }
            
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            
            JOptionPane.showMessageDialog(this, scrollPane, "Exploit Data", JOptionPane.PLAIN_MESSAGE);
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchExploits(String searchText) {
        searchColumn = "ID";
        searchDatabase(searchText, "Exploits");
    }
    
    private void searchDatabase(String searchText, String tableName) {
        try {
            Statement search = conn.createStatement();
            String query = "SELECT * FROM " + tableName + " WHERE '" + searchColumn + "' LIKE '%" + searchText + "%'";
            ResultSet rs = search.executeQuery(query);
            
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
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
            JScrollPane scrollPane = new JScrollPane(table);
            
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
