import javax.jmdns.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 
 * BIG OL' MESS
 */
public class VulnTab extends JPanel {
    private ArrayList<String> currentIPs = new ArrayList<>();
    private ArrayList<String> openPorts = new ArrayList<>();
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
                    openPorts.add(Integer.toString(port));
                } else {
                    System.out.println("PORT NOT FOUND " + port + " FOR IP: " + ipAddress);
                }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            // Update table with open ports
            //tableModel.setValueAt(ipAddress, row, 1);
            //tableModel.setValueAt(openPorts.toString(), row, 2);
            System.out.println("FINSHED port scanning for " + ipAddress + " with open ports: ");
            for(int i = 0; i < openPorts.size(); i++) {
                System.out.println(openPorts.get(i));
            }
            System.out.println("Operating System Detected: " + new ScanOperatingSystem(ipAddress).getOperatingSystem());
            System.out.println("Service Versions: " + getServiceVersions(ipAddress, openPorts));
    }




private class ScanOperatingSystem {
    private String ipAddress;

    public ScanOperatingSystem(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOperatingSystem() {
        try {
            URL url = new URL("http://" + ipAddress);
            URLConnection conn = url.openConnection();
            String userAgent = conn.getHeaderField("User-Agent");

            String os = "Unknown OS";
            if (userAgent != null) {
                Pattern pattern = Pattern.compile("\\((.*?)\\)");
                Matcher matcher = pattern.matcher(userAgent);
                if (matcher.find()) {
                    os = matcher.group(1).split(";")[0];
                }
            }

            return os;
        } catch (MalformedURLException e) {
            // Handle specific exceptions
            System.err.println("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error opening connection: " + e.getMessage());
        }
        return "Unknown OS";
    }

}

}
    public String getServiceVersions(String ipAddress, ArrayList<String> openPorts) {
        final String SERVICE_TYPE = "_http._tcp.local.";
        StringBuilder serviceVersions = new StringBuilder();

        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            try (JmDNS jmdns = JmDNS.create(address)) {
                for (String port : openPorts) {
                    ServiceInfo serviceInfo = jmdns.getServiceInfo(SERVICE_TYPE, "service" + port);
                    if (serviceInfo != null) {
                        String serviceName = serviceInfo.getName();
                        String serviceVersion = serviceInfo.getApplication();
                        serviceVersions.append(serviceName).append(": ").append(serviceVersion).append("\n");
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("The IP address could not be determined: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        }

        return serviceVersions.toString();}

}
