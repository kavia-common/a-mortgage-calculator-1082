package com.mortgagecalculator.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for mortgage calculations and formatting.
 */
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
    
    /**
     * Calculates the monthly payment for a mortgage.
     * 
     * @param principal The loan amount
     * @param annualInterestRate The annual interest rate in percentage (e.g., 5.5 for 5.5%)
     * @param loanTermInYears The loan term in years
     * @return The monthly payment amount
     */
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
    
    /**
     * Calculates the total payment over the life of the loan.
     * 
     * @param monthlyPayment The monthly payment amount
     * @param loanTermInYears The loan term in years
     * @return The total payment amount
     */
    public static double calculateTotalPayment(double monthlyPayment, int loanTermInYears) {
        return monthlyPayment * loanTermInYears * 12;
    }
    
    /**
     * Calculates the total interest paid over the life of the loan.
     * 
     * @param totalPayment The total payment amount
     * @param principal The loan amount
     * @return The total interest amount
     */
    public static double calculateTotalInterest(double totalPayment, double principal) {
        return totalPayment - principal;
    }
    
    /**
     * Formats a monetary value as currency.
     * 
     * @param value The value to format
     * @return The formatted currency string
     */
    public static String formatCurrency(double value) {
        return CURRENCY_FORMATTER.format(value);
    }
    
    /**
     * Formats a percentage value.
     * 
     * @param value The value to format (e.g., 5.5 for 5.5%)
     * @return The formatted percentage string
     */
    public static String formatPercent(double value) {
        return PERCENT_FORMATTER.format(value / 100);
    }
    
    /**
     * Parses a string input to a double value.
     * 
     * @param input The string input
     * @return An Optional containing the parsed double, or empty if parsing failed
     */
    public static Optional<Double> parseDouble(String input) {
        try {
            double value = Double.parseDouble(input.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Parses a string input to an integer value.
     * 
     * @param input The string input
     * @return An Optional containing the parsed integer, or empty if parsing failed
     */
    public static Optional<Integer> parseInt(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}