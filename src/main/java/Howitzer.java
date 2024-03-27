import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import java.sql.*;


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

	
        
        public static Connection conn;
        
        ArrayList<String> reports = new ArrayList<>();
        ArrayList<String> selectedScopes = new ArrayList<>();

	public ArrayList<String> currentIPs = new ArrayList<>();

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

            try {
                String url = "jdbc:mariadb://localhost:3306/";
                String dbName = "HowitzerDB";
                String driver = "org.mariadb.jdbc.Driver";
                String userName = "root";
                String password = "";
                
                Class.forName(driver);
                conn = DriverManager.getConnection(url + dbName, userName, password);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            selectScopeTab = new SelectScope(currentIPs);
	    scanNetworkTab = new ScanNetwork(selectScopeTab); /* Seperate tabs should be seperate classes on different files which extend JPanel */
            viewCVETab = new ViewCVE(conn);
            crossReferenceTab = new CrossReference();
            identifyVulnTab = new VulnTab(currentIPs);
            penetrateTab = new Penetrate(new ArrayList<>(), currentIPs);
            seeTrafficTab = new SeeTraffic();
            genReportTab = new Reporting(reports, selectedScopes);

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

            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1280,720);
            setVisible(true);
	}

	public class ActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == jMenuExit) {
				System.exit(0); //THIS CLOSES WITH NO SAVING OR ANYTHING DONT KEEP THIS TODO
			} else if (e.getSource() == genReportTab.printReportButton) {
                        // Generate the report when the Print Report button is clicked
                        try {
                            genReportTab.generateTXT();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
            }
		}
	}

	public static void main(String[] args) {
		new Howitzer();
	}
}


