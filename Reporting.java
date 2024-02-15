import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class Reporting extends JPanel {
    private ArrayList<String> reports;
    public JButton printReportButton;

    public Reporting(ArrayList<String> reports) {
        this.reports = reports;

        JPanel mainPanel = new JPanel();
        add(mainPanel);
        JLabel label = new JLabel("Reporting");
        mainPanel.add(label);

        printReportButton = new JButton("Print Report");
        printReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    printReports();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        mainPanel.add(printReportButton);
    }

    public void generatePDF(String filePath) throws Exception {
        try {
            PdfDocument pdfReport = new PdfDocument(new PdfWriter("report.pdf"));
            Document doc = new Document(pdfReport);
            String ipScan = "Scanned IP Addresses: ";
            Paragraph paragraphScans = new Paragraph(ipScan);
            for(String ipAddress : reports) {
                String scans = ipAddress;
                paragraphScans.add(ipAddress);
            }
            
            doc.add(paragraphScans);
            
            doc.close();
            JOptionPane.showMessageDialog(null, "Report. ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Reporting Error: " + e.getMessage());
        }
    }

    public void printReports() throws Exception {
        generatePDF("report.pdf");
    }
}
