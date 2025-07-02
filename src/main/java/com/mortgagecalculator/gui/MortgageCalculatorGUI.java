package com.mortgagecalculator.gui;

import com.mortgagecalculator.model.Mortgage;
import com.mortgagecalculator.util.MortgageCalculationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Use Java 8 lambdas for layout and component creation
        createAndShowGUI();
    }
    
    private void createAndShowGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Input panel at the top
        JPanel inputPanel = createInputPanel();
        
        // Results panel in the middle
        JPanel resultsPanel = createResultsPanel();
        
        // Amortization panel at the bottom
        JPanel amortizationPanel = createAmortizationPanel();
        
        // Add panels to the main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);
        mainPanel.add(amortizationPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Loan Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create input fields with labels using Java 8 functional approach
        Function<String, JLabel> createLabel = text -> new JLabel(text + ":");
        
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
        
        // Calculate button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(this::calculateMortgage);
        panel.add(calculateButton, gbc);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Mortgage Summary"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Create labels using Java 8 functional approach
        Function<String, JLabel> createLabel = text -> {
            JLabel label = new JLabel(text + ":");
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            return label;
        };
        
        Function<String, JLabel> createValueLabel = text -> {
            JLabel label = new JLabel(text);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            return label;
        };
        
        // Monthly payment
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(createLabel.apply("Monthly Payment"), gbc);
        
        gbc.gridx = 1;
        monthlyPaymentValueLabel = createValueLabel.apply("$0.00");
        panel.add(monthlyPaymentValueLabel, gbc);
        
        // Total payment
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(createLabel.apply("Total Payment"), gbc);
        
        gbc.gridx = 1;
        totalPaymentValueLabel = createValueLabel.apply("$0.00");
        panel.add(totalPaymentValueLabel, gbc);
        
        // Total interest
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel.apply("Total Interest"), gbc);
        
        gbc.gridx = 1;
        totalInterestValueLabel = createValueLabel.apply("$0.00");
        panel.add(totalInterestValueLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAmortizationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Amortization Schedule (Yearly)"));
        
        // Create table model and table
        String[] columnNames = {"Year", "Payment", "Principal", "Interest", "Remaining Balance"};
        tableModel = new DefaultTableModel(columnNames, 0);
        amortizationTable = new JTable(tableModel);
        
        // Use Java 8 streams to set column widths
        IntStream.range(0, columnNames.length)
                .forEach(i -> amortizationTable.getColumnModel().getColumn(i)
                        .setPreferredWidth(i == 0 ? 50 : 150));
        
        JScrollPane scrollPane = new JScrollPane(amortizationTable);
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