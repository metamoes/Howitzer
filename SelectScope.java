import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class SelectScope extends JPanel {

    public JTable scopeTable; 
	public DefaultTableModel scopeTableModel = new DefaultTableModel();

    public SelectScope() {
        JPanel mainPanel = new JPanel();
        add(mainPanel);
        

        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        centerPanel.setLayout(new GridLayout(1,1));

        JLabel label = new JLabel("SelectScope");
        topPanel.add(label);

        scopeTable = new JTable(scopeTableModel);

        scopeTable.setDragEnabled(false);
		scopeTableModel.addColumn("IP Address");
        scopeTableModel.addColumn("Remove"); // will be a button at some point
        scopeTable.setEnabled(false);

        centerPanel.add(scopeTable);
    }
}
