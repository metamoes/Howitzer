import java.awt.*;

import javax.management.ObjectName;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.util.EventObject;

public class SelectScope extends JPanel {

    public JTable scopeTable; 
	public DaniTableModel scopeTableModel;
	//JButton tButton = new JButton();

    public SelectScope() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
            

        JPanel topPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Current Scope");
        topPanel.add(label);

        scopeTableModel = new DaniTableModel();
        scopeTable = new JTable(scopeTableModel);

        scopeTable.setDragEnabled(false);
        scopeTable.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        scopeTable.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox()));
        scopeTable.setEnabled(true);

        centerPanel.add(new JScrollPane(scopeTable));
    }

    class DaniTableModel extends DefaultTableModel {
        public DaniTableModel() {
            super(new Object[]{"IP Address", "Remove"},0);
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 1) return true;
            return false;
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int editedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                editedRow = scopeTable.getEditingRow();
                fireEditingStopped();
            });
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() { //TODO, deleting the bottom row of the table crashes the awt-eventqueue thread and breaks the whole system :3
            if (isPushed) {
                scopeTableModel.removeRow(scopeTable.convertRowIndexToModel(editedRow));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
