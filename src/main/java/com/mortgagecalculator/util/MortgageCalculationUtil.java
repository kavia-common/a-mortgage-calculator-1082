package com.mortgagecalculator.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

public class MortgageCalculationUtil {
    
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);
    private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance(Locale.US);
    
    static {
        PERCENT_FORMATTER.setMinimumFractionDigits(2);
        PERCENT_FORMATTER.setMaximumFractionDigits(2);
    }
    
    private MortgageCalculationUtil() {
        // Private constructor to prevent instantiation
    }
    
    public static double calculateMonthlyPayment(double principal, double annualInterestRate, int loanTermInYears) {
        double monthlyInterestRate = annualInterestRate / 12 / 100; // Convert to monthly decimal
        int numberOfPayments = loanTermInYears * 12;
        
        if (monthlyInterestRate > 0) {
            return principal * 
                    (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numberOfPayments)) / 
                    (Math.pow(1 + monthlyInterestRate, numberOfPayments) - 1);
        } else {
            // If interest rate is 0, simple division
            return principal / numberOfPayments;
        }
    }
    
    public static double calculateTotalPayment(double monthlyPayment, int loanTermInYears) {
        return monthlyPayment * loanTermInYears * 12;
    }
    
    public static double calculateTotalInterest(double totalPayment, double principal) {
        return totalPayment - principal;
    }
    
    public static String formatCurrency(double value) {
        return CURRENCY_FORMATTER.format(value);
    }
    
    public static String formatPercent(double value) {
        return PERCENT_FORMATTER.format(value / 100);
    }
    
    public static Optional<Double> parseDouble(String input) {
        try {
            double value = Double.parseDouble(input.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    public static Optional<Integer> parseInt(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}