import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


public class Howitzer extends JFrame {
	JMenuBar menuBar;
	JMenu jMenuFile;
	JMenuItem jMenuExit;
	JMenu jMenuEdit;
	JMenu jMenuPref;

	public ScanNetwork scanNetworkTab;
	SelectScope selectScopeTab;
	ViewCVE viewCVETab;
	CrossReference crossReferenceTab;
	VulnTab identifyVulnTab;
	Penetrate penetrateTab;
	SeeTraffic seeTrafficTab;
	Reporting genReportTab;

	InetAddress ip;

	boolean scanning = false;
        
        ArrayList<String> reports = new ArrayList<>();

	public Howitzer() {
		super("Howitzer"); /* Sets the title of the window */
		menuBar = new JMenuBar(); /* All of this menubar stuff is just placeholder, none of it has a use yet */
		setJMenuBar(menuBar);

		jMenuFile = new JMenu("File");
		jMenuFile.setMnemonic('F');
		menuBar.add(jMenuFile);

		jMenuExit = new JMenuItem("Exit"); /* This is actually rigged to close the application :3 */
		jMenuExit.setMnemonic('x'); /* If used in the future, please ensure anything important is saved first */
		jMenuFile.add(jMenuExit);
	
		jMenuEdit = new JMenu("Edit");
		jMenuEdit.setMnemonic('E');
		menuBar.add(jMenuEdit);

		jMenuPref = new JMenu("Preferences");
		jMenuPref.setMnemonic('P');
		menuBar.add(jMenuPref);

		JPanel mainPanel = new JPanel();
		add(mainPanel);
		mainPanel.setLayout(new GridLayout(1,1));

		scanNetworkTab = new ScanNetwork(); /* Seperate tabs should be seperate classes on different files which extend JPanel */
		selectScopeTab = new SelectScope();
		viewCVETab = new ViewCVE();
		crossReferenceTab = new CrossReference();
		identifyVulnTab = new VulnTab();
		penetrateTab = new Penetrate();
		seeTrafficTab = new SeeTraffic();
		genReportTab = new Reporting(reports);

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("Scan Network", scanNetworkTab);
		tabPane.addTab("Select Scope", selectScopeTab);
		tabPane.addTab("View CVEs", viewCVETab);
		tabPane.addTab("Cross-Reference", crossReferenceTab);
		tabPane.addTab("Identify Vulns", identifyVulnTab);
		tabPane.addTab("Penetrate", penetrateTab);
		tabPane.addTab("Network Traffic", seeTrafficTab);
		tabPane.addTab("Generate Report", genReportTab);
		mainPanel.add(tabPane);

		ActionHandler ah = new ActionHandler();

		jMenuExit.addActionListener(ah);
		scanNetworkTab.scanButton.addActionListener(ah);
		scanNetworkTab.stopButton.addActionListener(ah);
		scanNetworkTab.sendButton.addActionListener(ah);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1280,720);
		setVisible(true);
	}

	public class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jMenuExit) {
				System.exit(0); //THIS CLOSES WITH NO SAVING OR ANYTHING DONT KEEP THIS TODO
			} else if (e.getSource() == scanNetworkTab.scanButton) {
				try {
				if (scanNetworkTab.fieldToAddr(scanNetworkTab.ipField.getText()) == null) {
					JOptionPane.showMessageDialog(scanNetworkTab, "IP is not correct.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				scanNetworkTab.ipTableModel.setRowCount(0);
				scanState(true);
				scanNetworkTab.subnetCalc(scanNetworkTab.fieldToAddr(scanNetworkTab.ipField.getText()), scanNetworkTab.fieldToAddr(scanNetworkTab.ipSubnet.getText()));
				ScanThread t = new ScanThread(scanNetworkTab.ipTableModel, reports);
				t.start();
				} catch (Exception ex) {}
					
			} else if (e.getSource() == scanNetworkTab.stopButton) {
				scanState(false);
			} else if (e.getSource() == scanNetworkTab.sendButton) {
				String[] a = scanNetworkTab.getCurrentTableAddresses();
				for (int i=0; i<a.length; i++) {
					selectScopeTab.scopeTableModel.insertRow(0, new Object[] { a[i] });
				}
			}
		}
	}

	/*
	 * Thread for scanNetworkTab, I don't know how to move this over to its respective file,
	 * It's late and I am sleepy.... TODO
	 */
	public class ScanThread extends Thread { 
		private final DefaultTableModel ipTableModel;
                private final ArrayList<String> reports;
    
                public ScanThread(DefaultTableModel ipTableModel, ArrayList<String> reports) {
                    this.ipTableModel = ipTableModel;
                    this.reports = reports;
                }

        /*private ScanThread() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }*/ //NOTE: idk what this is but it was giving a warning so i have commented it out.
		
		public void run() {
			try {
			byte[] startBytes = scanNetworkTab.fieldToAddr(scanNetworkTab.ipField.getText());
			byte[] endBytes = scanNetworkTab.subnetCalc(startBytes, scanNetworkTab.fieldToAddr(scanNetworkTab.ipSubnet.getText()));

			InetAddress startAddress = InetAddress.getByAddress(startBytes);
			InetAddress endAddress = InetAddress.getByAddress(endBytes);
			while (!startAddress.equals(endAddress)) {
				if (!scanning) return;
				boolean reached = startAddress.isReachable(100);
				System.out.println("REACHED: " + reached + " " + startAddress);
				if (reached) {
					scanNetworkTab.ipTableModel.insertRow(0, new Object[] { startAddress.getHostAddress(), startAddress.getHostName(), false });
                                        reports.add(startAddress.getHostAddress());
				} 
				incrementIP(startBytes);
				startAddress = InetAddress.getByAddress(startBytes);
			}
			scanState(false);
			} catch (Exception ex) {}
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
	}

	public void scanState(boolean t) {
		scanNetworkTab.scanButton.setEnabled(!t);
		scanNetworkTab.stopButton.setEnabled(t);
		scanNetworkTab.ipTable.setEnabled(!t);
		scanning = t; 
	}
	
	public static void main(String[] args) {
		new Howitzer();
	}
}


