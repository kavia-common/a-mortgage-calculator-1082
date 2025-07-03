package com.mortgagecalculator.gui;

import com.mortgagecalculator.model.Mortgage;
import com.mortgagecalculator.util.MortgageCalculationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Graphical user interface for the mortgage calculator.
 */
public class MortgageCalculatorGUI extends JFrame {

    // Warmer light orange color for background
    private static final Color WARM_LIGHT_ORANGE_BACKGROUND = new Color(255, 218, 185);
    // Dark green color for the calculate button
    private static final Color DARK_GREEN_BUTTON = new Color(34, 139, 34);

    private JTextField principalField;
    private JTextField interestRateField;
    private JTextField loanTermField;
    private JLabel monthlyPaymentValueLabel;
    private JLabel totalPaymentValueLabel;
    private JLabel totalInterestValueLabel;
    private JTable amortizationTable;
    private DefaultTableModel tableModel;
    
    public MortgageCalculatorGUI() {
        setTitle("Mortgage Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Increased size to accommodate all sections plus 25 rows in amortization table
        setSize(1000, 950);
        setLocationRelativeTo(null);
        
        // Set warm light orange background for the frame
        getContentPane().setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        // Use Java 8 lambdas for layout and component creation
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        // Create a container for input and results panels
        JPanel topContainer = new JPanel(new BorderLayout(10, 10));
        topContainer.setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        // Input panel at the top
        JPanel inputPanel = createInputPanel();
        
        // Results panel in the middle
        JPanel resultsPanel = createResultsPanel();
        
        // Amortization panel at the bottom
        JPanel amortizationPanel = createAmortizationPanel();
        
        // Add input and results to top container
        topContainer.add(inputPanel, BorderLayout.NORTH);
        topContainer.add(resultsPanel, BorderLayout.CENTER);
        
        // Add panels to the main panel
        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(amortizationPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        
        // Create titled border with matching font size
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Loan Information");
        titledBorder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.setBorder(titledBorder);
        panel.setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Create input fields with labels using Java 8 functional approach with larger font
        Function<String, JLabel> createLabel = text -> {
            JLabel label = new JLabel(text + ":");
            label.setFont(new Font("SansSerif", Font.PLAIN, 16));
            return label;
        };
        
        // Principal amount
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel.apply("Loan Amount ($)"), gbc);
        
        gbc.gridx = 1;
        principalField = new JTextField(10);
        panel.add(principalField, gbc);
        
        // Interest rate
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel.apply("Annual Interest Rate (%)"), gbc);
        
        gbc.gridx = 1;
        interestRateField = new JTextField(10);
        panel.add(interestRateField, gbc);
        
        // Loan term
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel.apply("Loan Term (years)"), gbc);
        
        gbc.gridx = 1;
        loanTermField = new JTextField(10);
        panel.add(loanTermField, gbc);
        
        // Calculate button with dark green background and white text
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 8, 8);
        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(DARK_GREEN_BUTTON);
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        calculateButton.setOpaque(true);
        calculateButton.setBorderPainted(false);
        calculateButton.addActionListener(this::calculateMortgage);
        panel.add(calculateButton, gbc);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        
        // Create titled border with matching font size
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Mortgage Summary");
        titledBorder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.setBorder(titledBorder);
        panel.setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        // Set explicit preferred size to ensure adequate space for all labels
        panel.setPreferredSize(new Dimension(400, 180));
        panel.setMinimumSize(new Dimension(400, 180));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        // Generous vertical spacing between components
        gbc.insets = new Insets(15, 10, 15, 10);
        
        // Create labels using Java 8 functional approach with larger font
        Function<String, JLabel> createLabel = text -> {
            JLabel label = new JLabel(text + ":");
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setFont(new Font("SansSerif", Font.PLAIN, 16));
            return label;
        };
        
        Function<String, JLabel> createValueLabel = text -> {
            JLabel label = new JLabel(text);
            label.setFont(new Font("SansSerif", Font.BOLD, 16));
            return label;
        };
        
        // Monthly payment
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.4;
        panel.add(createLabel.apply("Monthly Payment"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        monthlyPaymentValueLabel = createValueLabel.apply("$0.00");
        panel.add(monthlyPaymentValueLabel, gbc);
        
        // Total payment
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.4;
        panel.add(createLabel.apply("Total Payment"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        totalPaymentValueLabel = createValueLabel.apply("$0.00");
        panel.add(totalPaymentValueLabel, gbc);
        
        // Total interest
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.4;
        panel.add(createLabel.apply("Total Interest"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.6;
        totalInterestValueLabel = createValueLabel.apply("$0.00");
        panel.add(totalInterestValueLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAmortizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create titled border with matching font size
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Amortization Schedule (Yearly)");
        titledBorder.setTitleFont(new Font("SansSerif", Font.PLAIN, 16));
        panel.setBorder(titledBorder);
        panel.setBackground(WARM_LIGHT_ORANGE_BACKGROUND);
        
        // Create table model and table
        String[] columnNames = {"Year", "Payment", "Principal", "Interest", "Remaining Balance"};
        tableModel = new DefaultTableModel(columnNames, 0);
        amortizationTable = new JTable(tableModel);
        
        // Set larger font for table
        amortizationTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        amortizationTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Set row height to accommodate larger font
        amortizationTable.setRowHeight(20);
        
        // Use Java 8 streams to set column widths
        IntStream.range(0, columnNames.length)
                .forEach(i -> amortizationTable.getColumnModel().getColumn(i)
                        .setPreferredWidth(i == 0 ? 50 : 150));
        
        JScrollPane scrollPane = new JScrollPane(amortizationTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Set preferred size to show approximately 25 rows
        scrollPane.setPreferredSize(new Dimension(0, 545));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void calculateMortgage(ActionEvent e) {
        // Parse input values with validation
        Optional<Double> principalOpt = MortgageCalculationUtil.parseDouble(principalField.getText());
        if (!principalOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for loan amount.");
            principalField.requestFocus();
            return;
        }
        
        Optional<Double> interestRateOpt = MortgageCalculationUtil.parseDouble(interestRateField.getText());
        if (!interestRateOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for interest rate.");
            interestRateField.requestFocus();
            return;
        }
        
        Optional<Integer> loanTermOpt = MortgageCalculationUtil.parseInt(loanTermField.getText());
        if (!loanTermOpt.isPresent()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for loan term.");
            loanTermField.requestFocus();
            return;
        }
        
        // Extract values
        double principal = principalOpt.get();
        double annualInterestRate = interestRateOpt.get();
        int loanTermInYears = loanTermOpt.get();
        
        // Create a Mortgage object
        Mortgage mortgage = new Mortgage(principal, annualInterestRate, loanTermInYears);
        
        // Update result labels
        monthlyPaymentValueLabel.setText(MortgageCalculationUtil.formatCurrency(mortgage.getMonthlyPayment()));
        totalPaymentValueLabel.setText(MortgageCalculationUtil.formatCurrency(mortgage.getTotalPayment()));
        totalInterestValueLabel.setText(MortgageCalculationUtil.formatCurrency(mortgage.getTotalInterest()));
        
        // Generate and display amortization schedule
        updateAmortizationTable(mortgage);
    }
    
    private void updateAmortizationTable(Mortgage mortgage) {
        // Clear the table
        tableModel.setRowCount(0);
        
        // Get the yearly amortization schedule
        Mortgage.PaymentRecord[] yearlySchedule = mortgage.generateYearlyAmortizationSchedule();
        
        // Add to the table model
        for (Mortgage.PaymentRecord record : yearlySchedule) {
            Object[] rowData = {
                record.getPaymentNumber(),
                MortgageCalculationUtil.formatCurrency(record.getPayment()),
                MortgageCalculationUtil.formatCurrency(record.getPrincipalPayment()),
                MortgageCalculationUtil.formatCurrency(record.getInterestPayment()),
                MortgageCalculationUtil.formatCurrency(record.getRemainingBalance())
            };
            tableModel.addRow(rowData);
        }
    }
    
    public static void main(String[] args) {
        // Use Java 8 lambda for event dispatch thread
        SwingUtilities.invokeLater(() -> {
            MortgageCalculatorGUI calculator = new MortgageCalculatorGUI();
            calculator.setVisible(true);
        });
    }
}