import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.table.*;

import java.util.Set;
import java.util.concurrent.*;

public class ScanNetwork extends JPanel {

    public JButton scanButton; /* These should all be public so that Howitzer.java can access them */
    public JButton stopButton;
    private JLabel ipFieldLabel; /* Obviously if it should be private then make it private */
    public JTextField ipField;
    private JLabel ipSubnetLabel;
    public JTextField ipSubnet;
    public JButton sendButton;

    boolean scanning = false;
    int numHosts = 0;

    private ExecutorService executor;
    private CompletionService<Void> completionService;
    private Set<byte[]> uniqueIPSet = ConcurrentHashMap.newKeySet();

    public JTable ipTable; 
	public DefaultTableModel ipTableModel = new DefaultTableModel();

    private SelectScope selectScopeTab;

    public ScanNetwork(SelectScope ss) {
        selectScopeTab = ss;
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);


        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel bottomPanel = new JPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        topPanel.setLayout(new FlowLayout());

        scanButton = new JButton("SCAN");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        ipFieldLabel = new JLabel("IP: ");
        ipField = new JTextField("192.168.1.1", 15);
        ipSubnetLabel = new JLabel("Subnet: ");
        ipSubnet = new JTextField("255.255.255.0", 15);
        topPanel.add(scanButton);
        topPanel.add(stopButton);
        topPanel.add(ipFieldLabel);
        topPanel.add(ipField);
        topPanel.add(ipSubnetLabel);
        topPanel.add(ipSubnet);

        ipTable = new JTable(ipTableModel) {
            public Class<?> getColumnClass(int column) {
                return getValueAt(0, column).getClass();
            }
        };

		ipTable.setDragEnabled(false);
		ipTableModel.addColumn("IP Address");
		ipTableModel.addColumn("Hostname");
        ipTableModel.addColumn("Add to Scope");
        ipTable.setEnabled(false);
		centerPanel.add(new JScrollPane(ipTable));

        sendButton = new JButton("Send to Scope");
        bottomPanel.add(sendButton);
        sendButton.setEnabled(false);

        ActionHandler ah = new ActionHandler();
        scanButton.addActionListener(ah);
        stopButton.addActionListener(ah);
        sendButton.addActionListener(ah);

        executor = Executors.newFixedThreadPool(200);
        completionService = new ExecutorCompletionService<>(executor);
    }

    public class ActionHandler extends Component implements ActionListener {
		public void actionPerformed(ActionEvent e) {
        if (e.getSource() == scanButton) {
            try {
            if (stringToAddr(ipField.getText()) == null) {
                //JOptionPane.showMessageDialog(this, "IP is not correct.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            uniqueIPSet.removeAll(uniqueIPSet);
            ipTableModel.setRowCount(0);
            subnetCalc(stringToAddr(ipField.getText()), stringToAddr(ipSubnet.getText()));
            scanIpRange(5000);
            } catch (Exception ex) {}
                
        } else if (e.getSource() == stopButton) {
            shutdownScan();
            scanState(false); //TODO broke when switching to executor service, i think...
        } else if (e.getSource() == sendButton) {
            /*String[] a = getCurrentTableAddresses();
            for (String ip : a) {
                                //selectedScopes.add(ip);
                                selectScopeTab.addToScope(ip);
            }*/
            sendCurrentSelectedAddresses();
                        
        }
    }

    public void scanState(boolean t) {
		scanButton.setEnabled(!t);
		stopButton.setEnabled(t);
		ipTable.setEnabled(!t);
		scanning = t; 
		if (ipTable.getRowCount() > 0 && !t) {
			sendButton.setEnabled(!t);
		}
	}

    private void scanIpRange(int timeout) { //TODO I dont know how, or why, but sometimes this gets duplicate ip's
        
        byte[] ip = stringToAddr(ipField.getText());
        for (int i=0; i<numHosts; i++) {
            completionService.submit(new IpScannerTask(ip, timeout));
            incrementIP(ip);
        }

        scanState(true);
        try {
            for (int i=0; i<numHosts; i++) {
                completionService.take();
            }
            shutdownScan();
            scanState(false);
            if (uniqueIPSet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No live hosts were found.", "Information", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("No live hosts were found.");
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
    }

    public void shutdownScan() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    public byte[] stringToAddr(String in) {
        try {
            InetAddress addr = InetAddress.getByName(in);
            return addr.getAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] subnetCalc(byte[] startAddr, byte[] subnetMask) {
        byte[] useAbleStart = stringToAddr(ipField.getText());
        byte[] useAbleEnd = new byte[4];

        try {
        byte[] networkBytes = new byte[4];
        for (int i=0;i<4;i++) {
            networkBytes[i] = (byte)(startAddr[i] & subnetMask[i]);
        }
        
        int hosts = 1;
        for (int i=0;i<4;i++) {
            hosts *= 256 - (subnetMask[i] & 0xFF);
        }
        
        int endValue = 0;
        byte[] endAddr = new byte[4];
        for (int i=0;i<4;i++) {
            endValue += (networkBytes[i] & 0xFF) << (8*(3-i));
            int unsignedEndValue = endValue + hosts -1;
            endAddr[i] = (byte) ((unsignedEndValue >> (8*(3-i))) & 0xFF);
        }

        useAbleEnd = new byte[]{endAddr[0], endAddr[1], endAddr[2], (byte)(endAddr[3]-1)};
        InetAddress useAbleStartAddress = InetAddress.getByAddress(useAbleStart);
        InetAddress useAbleEndAddress = InetAddress.getByAddress(useAbleEnd);

        System.out.println("Start Address " + InetAddress.getByAddress(startAddr).getHostAddress());
        System.out.println("Subnet Mask " + InetAddress.getByAddress(subnetMask).getHostAddress());
        System.out.println("Network Address " + InetAddress.getByAddress(networkBytes).getHostAddress());
        System.out.println("Number of Hosts " + hosts);
        numHosts = hosts-2;
        System.out.println("Number of Usable Hosts " + (hosts-2));
        System.out.println("Range " + useAbleStartAddress.getHostAddress() + " - " + useAbleEndAddress.getHostAddress());
        return useAbleEnd; //for now this returns the end address
        } catch (Exception e) {
            return null;
        }
    }

    public void incrementIP(byte[] ipAddr) {
        for (int i = ipAddr.length - 1; i >= 0; i--) {
            if ((ipAddr[i] & 0xFF) == 255) {
                ipAddr[i] = 0;
            } else {
                ipAddr[i]++;
                break;
            }
        }
    }

    public void sendCurrentSelectedAddresses() {
        /* 
        String[] returnArray = new String[ipTableModel.getRowCount()];
        int j=0;
        for (int i=0; i<ipTableModel.getRowCount(); i++) {
            if ((boolean)ipTable.getValueAt(i, 2)) {
                returnArray[j] = ipTable.getValueAt(i, 0).toString();
                j++;
            }
        }
        
        return returnArray; */ //THIS OLD VERSION BROKE WITH THE NEW SCANNING SYSTEM.
        for (int i=0; i<ipTableModel.getRowCount(); i++) {
            if ((boolean)ipTable.getValueAt(i, 2)) {
                selectScopeTab.addToScope(ipTable.getValueAt(i, 0).toString());
            }
        }
    }

    private class IpScannerTask implements Callable<Void> {
        private byte[] ipAddress;
        private int timeout;

        public IpScannerTask(byte[] ipAddress, int timeout) {
            this.ipAddress = ipAddress;
            this.timeout = timeout;
        }

        @Override
        public Void call() {
            try {
            InetAddress ip = InetAddress.getByAddress(ipAddress);
            boolean reached = ip.isReachable(timeout);
            if (reached) {
                if (uniqueIPSet.add(ip.getAddress())) {
                ipTableModel.insertRow(0, new Object[] { ip.getHostAddress(), ip.getHostName(), false });
                //reports.add(startAddress.getHostAddress());
                System.out.println("REACHED: " + reached + " " + ip.getHostAddress());
                }
            }
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        } 
    }
}
}
