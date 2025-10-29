import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.awt.print.*;
// You might need to add JFreeChart library dependencies for advanced charts
// import org.jfree.chart.ChartFactory;
// import org.jfree.chart.ChartPanel;
// import org.jfree.chart.JFreeChart;
// import org.jfree.data.category.DefaultCategoryDataset;

public class billing extends JFrame implements ActionListener, Printable {

    // Existing fields...
    JTextField tfItem, tfPrice, tfDiscount, tfGST, tfCGST, tfFName, tfLName;
    JComboBox<Integer> cbQty;
    JTable table;
    DefaultTableModel model;
    JTextArea taBill;
    JButton btnAdd, btnTotal, btnPrint, btnClear;
    int billNo = 1001;
    double subtotal = 0;

    // Add new fields for inventory, sales, analysis, and payments
    JTabbedPane tabbedPane;
    DefaultTableModel inventoryModel;
    JTable inventoryTable;
    JTextArea salesLogArea;
    double totalSales = 0;
    
    JTextArea analysisArea;
    JTextArea paymentDetailsArea;

    billing() {
        setTitle("Smart Billing Management System");
        setSize(1200, 800);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        
        // Add all panels to the tabbed pane
        tabbedPane.addTab("Billing", createBillingPanel());
        tabbedPane.addTab("Inventory & Stock", createInventoryPanel());
        tabbedPane.addTab("Total Sales", createSalesPanel());
        tabbedPane.addTab("Analysis", createAnalysisPanel());
        tabbedPane.addTab("Payment Details", createPaymentPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Right Panel: Bill Preview
        taBill = new JTextArea(35, 30);
        taBill.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane billScroll = new JScrollPane(taBill);
        billScroll.setBorder(BorderFactory.createTitledBorder("Bill Preview"));
        add(billScroll, BorderLayout.EAST);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnTotal = new JButton("Generate Bill");
        btnTotal.addActionListener(this);
        btnPrint = new JButton("Print Bill");
        btnPrint.addActionListener(e -> printBill());
        btnClear = new JButton("Clear All");
        btnClear.addActionListener(e -> clearAll());

        bottomPanel.add(btnTotal);
        bottomPanel.add(btnPrint);
        bottomPanel.add(btnClear);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JPanel createBillingPanel() {
        // ... (existing billing panel code) ...
        JPanel billingPanel = new JPanel(new BorderLayout());

        // Top Panel: Customer Info
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        tfFName = new JTextField();
        tfLName = new JTextField();
        tfDiscount = new JTextField("5");
        tfGST = new JTextField("4");
        tfCGST = new JTextField("4");

        topPanel.add(new JLabel("First Name:"));
        topPanel.add(tfFName);
        topPanel.add(new JLabel("Last Name:"));
        topPanel.add(tfLName);
        topPanel.add(new JLabel("Discount %:"));
        topPanel.add(tfDiscount);
        topPanel.add(new JLabel("GST %:"));
        topPanel.add(tfGST);
        topPanel.add(new JLabel("CGST %:"));
        topPanel.add(tfCGST);
        
        billingPanel.add(topPanel, BorderLayout.NORTH);

        // Items Table
        String[] columns = {"Item Name", "Qty", "Price (₹)", "Subtotal"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Add Items"));

        JPanel itemPanel = new JPanel();
        tfItem = new JTextField(10);
        cbQty = new JComboBox<>();
        for (int i = 1; i <= 10; i++) cbQty.addItem(i);
        tfPrice = new JTextField(7);

        btnAdd = new JButton("Add Item");
        btnAdd.addActionListener(this);

        itemPanel.add(new JLabel("Item:"));
        itemPanel.add(tfItem);
        itemPanel.add(new JLabel("Qty:"));
        itemPanel.add(cbQty);
        itemPanel.add(new JLabel("Price:"));
        itemPanel.add(tfPrice);
        itemPanel.add(btnAdd);

        centerPanel.add(itemPanel, BorderLayout.NORTH);
        centerPanel.add(sp, BorderLayout.CENTER);
        billingPanel.add(centerPanel, BorderLayout.CENTER);

        return billingPanel;
    }

    private JPanel createInventoryPanel() {
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createTitledBorder("Inventory & Stock Management"));

        // Table for inventory
        String[] inventoryColumns = {"Item Name", "Stock Level", "Price"};
        inventoryModel = new DefaultTableModel(inventoryColumns, 0);
        inventoryTable = new JTable(inventoryModel);
        JScrollPane inventorySp = new JScrollPane(inventoryTable);
        
        // You can pre-populate the inventory for demonstration
        inventoryModel.addRow(new Object[]{"Laptop", 50, 50000.00});
        inventoryModel.addRow(new Object[]{"Mouse", 150, 500.00});
        inventoryModel.addRow(new Object[]{"Keyboard", 100, 1000.00});

        // Panel for adding new stock
        JPanel stockPanel = new JPanel(new FlowLayout());
        JTextField tfStockItem = new JTextField(10);
        JTextField tfStockPrice = new JTextField(7);
        JComboBox<Integer> cbStockQty = new JComboBox<>();
        for (int i = 1; i <= 50; i++) cbStockQty.addItem(i);
        JButton btnUpdateStock = new JButton("Add/Update Stock");
        btnUpdateStock.addActionListener(e -> {
            String item = tfStockItem.getText();
            int qty = (int) cbStockQty.getSelectedItem();
            double price = Double.parseDouble(tfStockPrice.getText());

            // Search for existing item
            for (int i = 0; i < inventoryModel.getRowCount(); i++) {
                if (inventoryModel.getValueAt(i, 0).equals(item)) {
                    int currentQty = (int) inventoryModel.getValueAt(i, 1);
                    inventoryModel.setValueAt(currentQty + qty, i, 1);
                    tfStockItem.setText("");
                    tfStockPrice.setText("");
                    return;
                }
            }
            // If item not found, add a new row
            inventoryModel.addRow(new Object[]{item, qty, price});
            tfStockItem.setText("");
            tfStockPrice.setText("");
        });

        stockPanel.add(new JLabel("Item:"));
        stockPanel.add(tfStockItem);
        stockPanel.add(new JLabel("Qty to add:"));
        stockPanel.add(cbStockQty);
        stockPanel.add(new JLabel("Price:"));
        stockPanel.add(tfStockPrice);
        stockPanel.add(btnUpdateStock);

        inventoryPanel.add(inventorySp, BorderLayout.CENTER);
        inventoryPanel.add(stockPanel, BorderLayout.SOUTH);

        return inventoryPanel;
    }

    private JPanel createSalesPanel() {
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.setBorder(BorderFactory.createTitledBorder("Total Sales"));

        salesLogArea = new JTextArea();
        salesLogArea.setEditable(false);
        JScrollPane salesScrollPane = new JScrollPane(salesLogArea);
        
        JButton btnRefreshSales = new JButton("Refresh Total Sales");
        btnRefreshSales.addActionListener(e -> displayTotalSales());

        salesPanel.add(salesScrollPane, BorderLayout.CENTER);
        salesPanel.add(btnRefreshSales, BorderLayout.SOUTH);

        return salesPanel;
    }
    
    private JPanel createAnalysisPanel() {
        JPanel analysisPanel = new JPanel(new BorderLayout());
        analysisPanel.setBorder(BorderFactory.createTitledBorder("Sales Analysis"));
        
        analysisArea = new JTextArea("Sales Analysis data and charts will appear here.");
        analysisArea.setEditable(false);
        JScrollPane analysisScrollPane = new JScrollPane(analysisArea);
        
        JButton btnRunAnalysis = new JButton("Run Analysis");
        btnRunAnalysis.addActionListener(e -> runAnalysis());
        
        analysisPanel.add(analysisScrollPane, BorderLayout.CENTER);
        analysisPanel.add(btnRunAnalysis, BorderLayout.SOUTH);
        
        return analysisPanel;
    }
    
    private JPanel createPaymentPanel() {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
        
        paymentDetailsArea = new JTextArea("Payment details will be logged here.");
        paymentDetailsArea.setEditable(false);
        JScrollPane paymentScrollPane = new JScrollPane(paymentDetailsArea);
        
        JButton btnShowPayments = new JButton("Show All Payments");
        btnShowPayments.addActionListener(e -> showPaymentDetails());
        
        paymentPanel.add(paymentScrollPane, BorderLayout.CENTER);
        paymentPanel.add(btnShowPayments, BorderLayout.SOUTH);
        
        return paymentPanel;
    }

    // Updated actionPerformed to handle inventory decrement
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            String item = tfItem.getText();
            int qty = (int) cbQty.getSelectedItem();
            double price = Double.parseDouble(tfPrice.getText());
            
            if (checkAndDecrementStock(item, qty)) {
                double sub = qty * price;
                subtotal += sub;
                model.addRow(new Object[]{item, qty, price, sub});
                tfItem.setText("");
                tfPrice.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Not enough stock for " + item, "Stock Alert", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == btnTotal) {
            generateBill();
        }
    }
    
    private boolean checkAndDecrementStock(String item, int qty) {
        for (int i = 0; i < inventoryModel.getRowCount(); i++) {
            if (inventoryModel.getValueAt(i, 0).equals(item)) {
                int currentQty = (int) inventoryModel.getValueAt(i, 1);
                if (currentQty >= qty) {
                    inventoryModel.setValueAt(currentQty - qty, i, 1);
                    return true;
                }
            }
        }
        return false;
    }

    // Updated generateBill to update total sales and payment details
    void generateBill() {
        String fName = tfFName.getText();
        String lName = tfLName.getText();
        double discount = Double.parseDouble(tfDiscount.getText());
        double gst = Double.parseDouble(tfGST.getText());
        double cgst = Double.parseDouble(tfCGST.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat stf = new SimpleDateFormat("hh:mm:ss a");

        String date = sdf.format(new Date());
        String time = stf.format(new Date());

        taBill.setText("========== BILL RECEIPT ==========\n");
        taBill.append("Customer: " + fName + " " + lName + "\n");
        taBill.append("Bill No: " + billNo + "\nDate: " + date + "  Time: " + time + "\n");
        taBill.append("----------------------------------\n");
        taBill.append(String.format("%-15s%-7s%-10s%-10s\n", "Item", "Qty", "Price", "Sub"));
        taBill.append("----------------------------------\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            taBill.append(String.format("%-15s%-7s%-10s%-10s\n",
                    model.getValueAt(i, 0),
                    model.getValueAt(i, 1),
                    model.getValueAt(i, 2),
                    model.getValueAt(i, 3)));
        }

        taBill.append("----------------------------------\n");
        taBill.append(String.format("Subtotal: ₹%.2f\n", subtotal));
        double discountAmt = subtotal * discount / 100;
        double gstAmt = (subtotal - discountAmt) * gst / 100;
        double cgstAmt = (subtotal - discountAmt) * cgst / 100;
        double total = subtotal - discountAmt + gstAmt + cgstAmt;
        taBill.append(String.format("Discount (%.1f%%): -₹%.2f\n", discount, discountAmt));
        taBill.append(String.format("GST (%.1f%%): ₹%.2f\n", gst, gstAmt));
        taBill.append(String.format("CGST (%.1f%%): ₹%.2f\n", cgst, cgstAmt));
        taBill.append("----------------------------------\n");
        taBill.append(String.format("Total Payable: ₹%.2f\n", total));
        taBill.append("==================================\n");

        // Update total sales
        totalSales += total;
        
        // Log payment details
        String paymentDetails = "Bill " + billNo + ": " + fName + " " + lName + " paid ₹" + String.format("%.2f", total) + "\n";
        paymentDetailsArea.append(paymentDetails);
        
        billNo++;
    }

    void displayTotalSales() {
        salesLogArea.setText("Total Sales: ₹" + String.format("%.2f", totalSales));
    }
    
    void runAnalysis() {
        // Placeholder for analysis logic. You could use JFreeChart here.
        analysisArea.setText("Generating sales analysis...\n");
        analysisArea.append("Top selling items:\n - Laptop\n - Mouse\n");
        analysisArea.append("\nRevenue by product (requires more data and logic)");
        
       
    }
    
    void showPaymentDetails() {
       
    }

    void printBill() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    void clearAll() {
        model.setRowCount(0);
        taBill.setText("");
        subtotal = 0;
    }

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;
        g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        taBill.printAll(g);
        return PAGE_EXISTS;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new billing());
    }
}