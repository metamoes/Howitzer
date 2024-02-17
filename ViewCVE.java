import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ViewCVE extends JPanel {

    private Connection conn;
    
    public ViewCVE(Connection conn) {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("ViewCVE");
        mainPanel.add(label);
        this.conn = conn;
        setupGUI();
        setupDatabase();
    }
    
    private void setupDatabase() {
        
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
            while (rs.next()) {
                System.out.println("CVE Number: " + rs.getString("CVE Number"));
                System.out.println("Status: " + rs.getString("Status"));
                System.out.println("Description: " + rs.getString("Description"));
                System.out.println("References: " + rs.getString("References"));
            }
            
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
            while (rs.next()) {
                System.out.println("Portlist Number: " + rs.getString("Portlist Number"));
                System.out.println("Status: " + rs.getString("Status"));
                System.out.println("Description: " + rs.getString("Description"));
                System.out.println("References: " + rs.getString("References"));
            }
            
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
            while (rs.next()) {
                System.out.println("Exploit Number: " + rs.getString("Exploit Number"));
                System.out.println("Status: " + rs.getString("Status"));
                System.out.println("Description: " + rs.getString("Description"));
                System.out.println("References: " + rs.getString("References"));
            }
            
            view.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
