package com.mortgagecalculator.cli;

import com.mortgagecalculator.model.Mortgage;
import com.mortgagecalculator.util.MortgageCalculationUtil;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

public class MortgageCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Lambda supplier for input validation
        Supplier<Optional<Double>> getPrincipal = () -> {
            try {
                System.out.print("Enter loan amount (e.g., 250000): $");
                double principal = scanner.nextDouble();
                return principal > 0 ? Optional.of(principal) : Optional.empty();
            } catch (InputMismatchException e) {
                scanner.next(); // Clear invalid input
                return Optional.empty();
            }
        };
        
        Supplier<Optional<Double>> getAnnualInterestRate = () -> {
            try {
                System.out.print("Enter annual interest rate (e.g., 5.5 for 5.5%): ");
                double rate = scanner.nextDouble();
                return rate > 0 ? Optional.of(rate) : Optional.empty();
            } catch (InputMismatchException e) {
                scanner.next(); // Clear invalid input
                return Optional.empty();
            }
        };
        
        Supplier<Optional<Integer>> getLoanTermInYears = () -> {
            try {
                System.out.print("Enter loan term in years (e.g., 30): ");
                int term = scanner.nextInt();
                return term > 0 ? Optional.of(term) : Optional.empty();
            } catch (InputMismatchException e) {
                scanner.next(); // Clear invalid input
                return Optional.empty();
            }
        };
        
        // Get valid principal
        Optional<Double> principalOpt;
        do {
            principalOpt = getPrincipal.get();
            if (!principalOpt.isPresent()) {
                System.out.println("Please enter a valid positive number for loan amount.");
            }
        } while (!principalOpt.isPresent());
        
        // Get valid interest rate
        Optional<Double> annualInterestRateOpt;
        do {
            annualInterestRateOpt = getAnnualInterestRate.get();
            if (!annualInterestRateOpt.isPresent()) {
                System.out.println("Please enter a valid positive number for interest rate.");
            }
        } while (!annualInterestRateOpt.isPresent());
        
        // Get valid loan term
        Optional<Integer> loanTermOpt;
        do {
            loanTermOpt = getLoanTermInYears.get();
            if (!loanTermOpt.isPresent()) {
                System.out.println("Please enter a valid positive number for loan term.");
            }
        } while (!loanTermOpt.isPresent());
        
        double principal = principalOpt.get();
        double annualInterestRate = annualInterestRateOpt.get();
        int loanTermInYears = loanTermOpt.get();
        
        // Create a Mortgage object
        Mortgage mortgage = new Mortgage(principal, annualInterestRate, loanTermInYears);
        
        // Display the results using Java 8 streams and lambdas
        System.out.println("\nMortgage Summary:");
        System.out.println("----------------");
        
        java.util.stream.Stream.of(
            "Loan Amount: " + MortgageCalculationUtil.formatCurrency(mortgage.getPrincipal()),
            "Annual Interest Rate: " + annualInterestRate + "%",
            "Loan Term: " + loanTermInYears + " years",
            "Monthly Payment: " + MortgageCalculationUtil.formatCurrency(mortgage.getMonthlyPayment()),
            "Total Payment: " + MortgageCalculationUtil.formatCurrency(mortgage.getTotalPayment()),
            "Total Interest: " + MortgageCalculationUtil.formatCurrency(mortgage.getTotalInterest())
        ).forEach(System.out::println);
        
        // Generate amortization schedule
        System.out.println("\nWould you like to see the amortization schedule? (y/n)");
        String choice = scanner.next().trim().toLowerCase();
        
        if (choice.equals("y")) {
            System.out.println("\nAmortization Schedule:");
            System.out.println("---------------------");
            System.out.printf("%-10s %-15s %-15s %-15s %-15s%n", 
                              "Payment #", "Payment", "Principal", "Interest", "Remaining");
            
            // Get the yearly amortization schedule
            Mortgage.PaymentRecord[] yearlySchedule = mortgage.generateYearlyAmortizationSchedule();
            
            // Display the yearly schedule
            for (Mortgage.PaymentRecord record : yearlySchedule) {
                System.out.printf("%-10s %-15s %-15s %-15s %-15s%n", 
                                 "Year " + record.getPaymentNumber(),
                                 MortgageCalculationUtil.formatCurrency(record.getPayment()),
                                 MortgageCalculationUtil.formatCurrency(record.getPrincipalPayment()),
                                 MortgageCalculationUtil.formatCurrency(record.getInterestPayment()),
                                 MortgageCalculationUtil.formatCurrency(record.getRemainingBalance()));
            }
        }
        
        scanner.close();
    }
}