import javax.jmdns.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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
    private JButton stop;
    private AtomicBoolean stopFlag = new AtomicBoolean(false);
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel progressLabel;

    public VulnTab(ArrayList<String> c) {

        currentIPs = c;
        tableModel = new DefaultTableModel(new Object[]{"IP Address", "Open Port"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        JPanel buttonPanel = new JPanel(); // Panel for the buttons
        JPanel labelPanel = new JPanel(); // Panel for the label

        JLabel label = new JLabel("VulnTab");
        buttonPanel.add(label);

        scan = new JButton("Scan");
        buttonPanel.add(scan);

        stop = new JButton("Stop");
        buttonPanel.add(stop);

        progressLabel = new JLabel("Progress: 0/65536"); // Initialize the label
        labelPanel.add(progressLabel); // Add the label to the label panel

        mainPanel.add(buttonPanel, BorderLayout.NORTH); // Add the button panel to the main panel
        mainPanel.add(labelPanel, BorderLayout.CENTER); // Add the label panel below the button panel

        ActionHandler ah = new ActionHandler();
        scan.addActionListener(ah);
        stop.addActionListener(ah);
        executor = Executors.newFixedThreadPool(10000);
        
    }

    public class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == scan) {
                stopFlag.set(false);
                scanPorts();
            } else if (e.getSource() == stop) {
                stopFlag.set(true);
            }
        }
    }

    public void scanPorts() {
        for (int i=0; i<currentIPs.size(); i++) {
            if (stopFlag.get()) {
                break;
            }
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
        private AtomicInteger portsScanned = new AtomicInteger(0);
        private final ExecutorService executor;
        //private DefaultTableModel tableModel;
        //private int row;

        public PortScannerTask(String ipAddress) {
            this.ipAddress = ipAddress;
            this.ipAddress = ipAddress;
            this.executor = Executors.newFixedThreadPool(1000);
            //this.tableModel = tableModel;
            //this.row = row;
        }

        @Override
        public void run() {
            System.out.println("STARTED port scanning for " + ipAddress);
            for (int port = 1; port <= 65536; port++) {
                if (stopFlag.get()) {
                    break;
                }
                final int finalPort = port;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (scanPort(ipAddress, finalPort, 2000)) {
                            System.out.println("PORT FOUND " + finalPort + " FOR IP: " + ipAddress);
                            openPorts.add(Integer.toString(finalPort));
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    tableModel.addRow(new Object[]{ipAddress, finalPort});
                                    int totalScanned = portsScanned.incrementAndGet(); // Increment the counter
                                    double percentage = (double) totalScanned / 65536 * 100; // percentage of completion
                                    progressLabel.setText("Progress: " + totalScanned + "/65536 (" + String.format("%.2f", percentage) + "%)"); // Update the label
                                }
                            });
                        } else {
                            System.out.println("PORT NOT FOUND " + finalPort + " FOR IP: " + ipAddress);
                            int totalScanned = portsScanned.incrementAndGet(); // Increment the counter
                            double percentage = (double) totalScanned / 65536 * 100; // percentage of completion
                            progressLabel.setText("Progress: " + totalScanned + "/65536 (" + String.format("%.2f", percentage) + "%)"); // Update the label
                        }
                    }
                });
            }
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            progressLabel.setText("Scanning Operating System");
            System.out.println("FINISHED port scanning for " + ipAddress);
            System.out.println("Operating System Detected: " + new ScanOperatingSystem(ipAddress).getOperatingSystem());
            progressLabel.setText("Scanning Service Versions");
            System.out.println("Service Versions: " + getServiceVersions(ipAddress, openPorts));
            progressLabel.setText("Scan Completed");

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
