import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;


public class Howitzer extends JFrame {
	JMenuBar menuBar;
	JMenu jMenuFile;
	JMenuItem jMenuExit;
	JMenu jMenuEdit;
	JMenu jMenuPref;

	JButton testButton;
	JButton testButton2;

	ScanNetwork scanNetworkTab;
	JPanel selectScopeTab;
	JPanel viewCVETab;
	JPanel crossReferenceTab;
	JPanel identifyVulnTab;
	JPanel penetrateTab;
	JPanel seeTrafficTab;
	JPanel genReportTab;

	InetAddress ip;

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

		JPanel tabPanel = new JPanel(); /* TODO, temporary tabs */
		testButton = new JButton("COOL BUTTON");
		tabPanel.add(testButton);

		JPanel tabPanel2 = new JPanel();
		testButton2 = new JButton("COOLER BUTTON");
		tabPanel2.add(testButton2);

		scanNetworkTab = new ScanNetwork(); /* Seperate tabs should be seperate classes on different files which extend JPanel */
		selectScopeTab = new JPanel();
		viewCVETab = new JPanel();
		crossReferenceTab = new JPanel();
		identifyVulnTab = new JPanel();
		penetrateTab = new JPanel();
		seeTrafficTab = new JPanel();
		genReportTab = new JPanel();

		JTabbedPane tabPane = new JTabbedPane();
		tabPane.addTab("TEST TAB", tabPanel);
		tabPane.addTab("TEST TAB2", tabPanel2);
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
		testButton.addActionListener(ah);
		testButton2.addActionListener(ah);
		scanNetworkTab.scanButton.addActionListener(ah);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1280,720);
		setVisible(true);
	}

	public class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jMenuExit) {
				System.exit(0); //THIS CLOSES WITH NO SAVING OR ANYTHING DONT KEEP THIS TODO
			} else if (e.getSource() == testButton) {
				System.out.printf("COOL BUTTON PRESSED\n");
			} else if (e.getSource() == testButton2) {
				System.out.printf("COOLER BUTTON PRESSED\n");
			} else if (e.getSource() == scanNetworkTab.scanButton) {
				try {
				/*
				ip = InetAddress.getByName(testBox.getText());
				boolean reached = ip.isReachable(1000);
				System.out.println("REACHED: " + reached + " " + ip);
				if (reached) {
					System.out.println(ip.getHostName());
				} */
				ScanThread t = new ScanThread();
				t.start();
				} catch (Exception ex) {}
					
			}
		}
	}

	/*
	 * Thread for scanNetworkTab, I don't know how to move this over to its respective file,
	 * It's late and I am sleepy.... TODO
	 */
	public class ScanThread extends Thread { 
		//private byte f;
		/*public ScanThread(byte f) {
			this.f = f;
		}*/
		public void run() {
			try {
			for (byte i = 0; i<254; i++) {
			InetAddress ip = InetAddress.getByAddress(new byte[] { (byte)192, (byte)168, (byte)1, i });
			boolean reached = ip.isReachable(100);
			System.out.println("REACHED: " + reached + " " + ip);
			if (reached) {
				scanNetworkTab.ipTableModel.insertRow(0, new Object[] { ip.getHostAddress(), ip.getHostName() });
			} } } catch (Exception ex) {}
		}
	}

	public static void main(String[] args) {
		new Howitzer();
	}
}


