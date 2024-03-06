import java.awt.event.*;
import javax.swing.*;
import java.net.*;


public class VulnTab extends JPanel {
    JButton scan;
    JTextField ipAddr;
    public VulnTab() {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("VulnTab");
        mainPanel.add(label);
        scan = new JButton("Scan");
        mainPanel.add(scan);
        ipAddr = new JTextField("192.168.1.64");
        mainPanel.add(ipAddr);

        ActionHandler ah = new ActionHandler();
        scan.addActionListener(ah);
        
        
    }   

    public class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == scan) {
                if (scanPort(ipAddr.getText(), 443, 200)) {
                    System.out.println("FOUND HTTPS");
                }
            }
        }
    }

    public boolean scanPort(String ip, int port, int timeout) {
        try {
            Socket s = new Socket();
            s.connect(new InetSocketAddress(ip,port), timeout);
            s.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
