import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import javax.swing.table.DefaultTableModel;

/*
 * 
 * BIG OL' MESS
 */
public class VulnTab extends JPanel {
    private ArrayList<String> currentIPs = new ArrayList<>();
    private JButton scan;
    private ExecutorService executor;

    public VulnTab(ArrayList<String> c) {
        currentIPs = c;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("VulnTab");
        topPanel.add(label);
        scan = new JButton("Scan");
        topPanel.add(scan);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        

        ActionHandler ah = new ActionHandler();
        scan.addActionListener(ah);
        
        executor = Executors.newFixedThreadPool(200);
        
    }   

    public class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == scan) {
                scanPorts();
            }
        }
    }

    public void scanPorts() {
        for (int i=0; i<currentIPs.size(); i++) {
            executor.execute(new PortScannerTask(currentIPs.get(i)));
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

    private class PortScannerTask implements Runnable {
        private String ipAddress;
        //private DefaultTableModel tableModel;
        //private int row;

        public PortScannerTask(String ipAddress) {
            this.ipAddress = ipAddress;
            //this.tableModel = tableModel;
            //this.row = row;
        }

        @Override
        public void run() {
            //StringBuilder openPorts = new StringBuilder();
            System.out.println("STARTED port scanning for " + ipAddress);
            for (int port = 1; port <= 65536; port++) {
                try {
                if (scanPort(ipAddress, port, 500)) {
                    System.out.println("PORT FOUND " + port + " FOR IP: " + ipAddress);
                } else {
                    //System.out.println("PORT NOT FOUND " + port + " FOR IP: " + ipAddress);
                }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            // Update table with open ports
            //tableModel.setValueAt(ipAddress, row, 1);
            //tableModel.setValueAt(openPorts.toString(), row, 2);
            System.out.println("FINSHED port scanning for " + ipAddress);
        }
    }

}
