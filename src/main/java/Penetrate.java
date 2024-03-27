import javax.swing.*;
import java.util.ArrayList;

public class Penetrate extends JPanel {
    private ArrayList<String> detectedVulns;
    private ArrayList<String> currentIPs;

    public Penetrate(ArrayList<String> vulnerabilities, ArrayList<String> currentIPs) {
        this.detectedVulns = vulnerabilities;
        this.currentIPs = currentIPs;
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("Penetrate");
        mainPanel.add(label);

        for (String vulnerability : detectedVulns) {
            JButton vulnButton = new JButton(vulnerability);
            mainPanel.add(vulnButton);
            vulnButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Exploiting " + vulnerability);
            });
        }

        JButton penButton = new JButton("Penetrate");
        mainPanel.add(penButton);
        penButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Penetrating with exploits");
        });
    }
}
