import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewCVE extends JPanel {

    private Connection conn;
    
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
        
    
        viewCVE.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewCVEData();
            }
        });
        
        viewPortlist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewPortlistData();
            }
        });
        
        viewExploits.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewExploitsData();
            }
        });
        
        add(viewCVE);
        add(viewPortlist);
        add(viewExploits);
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
    
    private void viewPortlistData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM Portlist");
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Portlist Number");
            model.addColumn("Status");
            model.addColumn("Description");
            model.addColumn("References");
            
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
    
    private void viewExploitsData() {
        try {
            Statement view = conn.createStatement();
            ResultSet rs = view.executeQuery("SELECT * FROM Exploits");
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Exploit Number");
            model.addColumn("Status");
            model.addColumn("Description");
            model.addColumn("References");
            
            while (rs.next()) {
                String[] row = {
                    rs.getString("Exploit Number"),
                    rs.getString("Status"),
                    rs.getString("Description"),
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
}
