import javax.swing.*;
import java.awt.*;
import java.net.*;

import javax.swing.table.*;

public class ScanNetwork extends JPanel {

    public JButton scanButton; /* These should all be public so that Howitzer.java can access them */
    public JButton stopButton;
    private JLabel ipFieldLabel; /* Obviously if it should be private then make it private */
    public JTextField ipField;
    private JLabel ipSubnetLabel;
    public JTextField ipSubnet;
    public JButton sendButton;

    public JTable ipTable; 
	public DefaultTableModel ipTableModel = new DefaultTableModel();

    public ScanNetwork() {
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

    }

    public byte[] fieldToAddr(String in) {
        try {
            InetAddress addr = InetAddress.getByName(in);
            return addr.getAddress();
        } catch (Exception e) {
            
            e.printStackTrace();
            return null;
        }
    }

    public byte[] subnetCalc(byte[] startAddr, byte[] subnetMask) {
        byte[] useAbleStart = fieldToAddr(ipField.getText());
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
        System.out.println("Number of Usabel Hosts " + (hosts-2));
        System.out.println("Range " + useAbleStartAddress.getHostAddress() + " - " + useAbleEndAddress.getHostAddress());
        return useAbleEnd; //for now this returns the end address
        } catch (Exception e) {
            return null;
        }
    }

    public String[] getCurrentTableAddresses() {
        String[] returnArray = new String[ipTableModel.getRowCount()];
        int j=0;
        for (int i=0; i<ipTableModel.getRowCount(); i++) {
            if ((boolean)ipTable.getValueAt(i, 2)) {
                returnArray[j] = ipTable.getValueAt(i, 0).toString();
                j++;
            }
        }
        return returnArray;
    }
}
