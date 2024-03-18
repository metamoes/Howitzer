import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.io.*;

/**
 *
 * @author irona
 */
public class Reporting extends JPanel {
    private ArrayList<String> reports;
    private ArrayList<String> selectedScopes;
    
    public JButton printReportButton;
    
    public void addSelectedScope(String scope) {
        selectedScopes.add(scope);
    }
    
    public Reporting(ArrayList<String> reports, ArrayList<String> selectedScopes) {
        this.reports = reports;
        this.selectedScopes = selectedScopes;
        
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("Reporting");
        mainPanel.add(label);

        printReportButton = new JButton("Print Report");
        printReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    generateTXT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mainPanel.add(printReportButton);
    }

    public void generateTXT() throws Exception {
        try {
            String getCurrent = System.getProperty("user.dir") + "/src";
            File file = new File(getCurrent, "report.txt");
            FileWriter writer = new FileWriter(file);
            writer.write("Scanned IP Addresses\n");
            for (String ipAddress: reports) {
                writer.write(ipAddress + "\n");
            }
            writer.write("\n");
            writer.write("Selected Scopes\n");
            
            for (String scope : selectedScopes) {
                if (scope != null) {
                    writer.write(scope + "\n");
                }
            }
            
            writer.close();
            JOptionPane.showMessageDialog(this, "Report generated.");
        } catch (IOException ex){
            System.out.println("Error in generating text.");
            ex.printStackTrace();
        }
    }
}
