import javax.swing.*;
import javax.swing.table.*;

public class ScanNetwork extends JPanel {

    public JButton scanButton; /* These should all be public so that Howitzer.java can access them */
    public JTable ipTable; /* Obviously if it should be private then make it private */
	public DefaultTableModel ipTableModel = new DefaultTableModel();

    public ScanNetwork() {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        scanButton = new JButton("SCAN");
        mainPanel.add(scanButton);
        
        ipTable = new JTable(ipTableModel);
		ipTable.setDragEnabled(false);
		ipTableModel.addColumn("IP Address");
		ipTableModel.addColumn("Hostname");
		mainPanel.add(new JScrollPane(ipTable));
    }
    
}
