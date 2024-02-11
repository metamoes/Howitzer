import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Reporting extends JPanel {
    private ArrayList<String> reports;
    public JButton printReportButton;
    
    public Reporting(ArrayList<String> reports) {
        this.reports = reports;
        
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("Reporting");
        mainPanel.add(label);
        
        printReportButton = new JButton("Print Report");
        printReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printReports();
            }
        });
    
        mainPanel.add(printReportButton);
    }
    
    public void printReports() {
        System.out.println("Scanned IP Addresses: ");
        for(String ipAddress : reports) {
            System.out.println(ipAddress);
        }
    }
}
