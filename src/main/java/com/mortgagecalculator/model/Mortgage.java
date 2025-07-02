package com.mortgagecalculator.model;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Represents a mortgage loan with its parameters and calculated values.
 */
public class Mortgage {
    private final double principal;
    private final double annualInterestRate;
    private final int loanTermInYears;
    
    // Derived values
    private final double monthlyInterestRate;
    private final int numberOfPayments;
    private final double monthlyPayment;
    private final double totalPayment;
    private final double totalInterest;
    
    /**
     * Creates a new Mortgage instance with the given parameters.
     * 
     * @param principal The loan amount
     * @param annualInterestRate The annual interest rate (percentage)
     * @param loanTermInYears The loan term in years
     */
    public Mortgage(double principal, double annualInterestRate, int loanTermInYears) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.loanTermInYears = loanTermInYears;
        
        // Calculate derived values
        this.monthlyInterestRate = annualInterestRate / 12 / 100; // Convert to monthly decimal
        this.numberOfPayments = loanTermInYears * 12;
        
        // Calculate monthly payment using mortgage formula
        if (monthlyInterestRate > 0) {
            this.monthlyPayment = principal * 
                (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, numberOfPayments)) / 
                (Math.pow(1 + monthlyInterestRate, numberOfPayments) - 1);
        } else {
            // If interest rate is 0, simple division
            this.monthlyPayment = principal / numberOfPayments;
        }
        
        this.totalPayment = monthlyPayment * numberOfPayments;
        this.totalInterest = totalPayment - principal;
    }
    
    /**
     * Generates an amortization schedule.
     * 
     * @return An array of PaymentRecord objects for each payment in the schedule
     */
    public PaymentRecord[] generateAmortizationSchedule() {
        PaymentRecord[] schedule = new PaymentRecord[numberOfPayments];
        // Create a wrapper class to hold the mutable state
        class BalanceHolder {
            double remainingBalance;
            
            BalanceHolder(double initial) {
                this.remainingBalance = initial;
            }
        }
        
        final BalanceHolder holder = new BalanceHolder(principal);
        
        // Use Java 8 streams to generate the schedule
        IntStream.range(0, numberOfPayments)
                .forEach(i -> {
                    double interestPayment = holder.remainingBalance * monthlyInterestRate;
                    double principalPayment = monthlyPayment - interestPayment;
                    
                    // Adjust the final payment to account for rounding errors
                    if (i == numberOfPayments - 1) {
                        principalPayment = holder.remainingBalance;
                        interestPayment = monthlyPayment - principalPayment;
                    }
                    
                    holder.remainingBalance -= principalPayment;
                    if (holder.remainingBalance < 0.01) holder.remainingBalance = 0; // Handle small decimal errors
                    
                    schedule[i] = new PaymentRecord(
                            i + 1,
                            monthlyPayment,
                            principalPayment,
                            interestPayment,
                            holder.remainingBalance
                    );
                });
        
        return schedule;
    }
    
    /**
     * Generates a yearly amortization schedule.
     * 
     * @return An array of PaymentRecord objects summarizing each year
     */
    public PaymentRecord[] generateYearlyAmortizationSchedule() {
        PaymentRecord[] monthlySchedule = generateAmortizationSchedule();
        PaymentRecord[] yearlySchedule = new PaymentRecord[loanTermInYears];
        
        // Use Java 8 streams to group by year
        IntStream.range(0, loanTermInYears)
                .forEach(year -> {
                    int startIndex = year * 12;
                    int endIndex = Math.min(startIndex + 11, numberOfPayments - 1);
                    
                    double yearlyPrincipal = 0;
                    double yearlyInterest = 0;
                    double yearlyPayment = 0;
                    
                    for (int i = startIndex; i <= endIndex; i++) {
                        yearlyPrincipal += monthlySchedule[i].getPrincipalPayment();
                        yearlyInterest += monthlySchedule[i].getInterestPayment();
                        yearlyPayment += monthlySchedule[i].getPayment();
                    }
                    
                    yearlySchedule[year] = new PaymentRecord(
                            year + 1,
                            yearlyPayment,
                            yearlyPrincipal,
                            yearlyInterest,
                            monthlySchedule[endIndex].getRemainingBalance()
                    );
                });
        
        return yearlySchedule;
    }
    
    // Getters
    public double getPrincipal() {
        return principal;
    }
    
    public double getAnnualInterestRate() {
        return annualInterestRate;
    }
    
    public int getLoanTermInYears() {
        return loanTermInYears;
    }
    
    public double getMonthlyInterestRate() {
        return monthlyInterestRate;
    }
    
    public int getNumberOfPayments() {
        return numberOfPayments;
    }
    
    public double getMonthlyPayment() {
        return monthlyPayment;
    }
    
    public double getTotalPayment() {
        return totalPayment;
    }
    
    public double getTotalInterest() {
        return totalInterest;
    }
    
    /**
     * Inner class representing a single payment or period in the amortization schedule.
     */
    public static class PaymentRecord {
        private final int paymentNumber;
        private final double payment;
        private final double principalPayment;
        private final double interestPayment;
        private final double remainingBalance;
        
        public PaymentRecord(int paymentNumber, double payment, double principalPayment, 
                            double interestPayment, double remainingBalance) {
            this.paymentNumber = paymentNumber;
            this.payment = payment;
            this.principalPayment = principalPayment;
            this.interestPayment = interestPayment;
            this.remainingBalance = remainingBalance;
        }
        
        public int getPaymentNumber() {
            return paymentNumber;
        }
        
        public double getPayment() {
            return payment;
        }
        
        public double getPrincipalPayment() {
            return principalPayment;
        }
        
        public double getInterestPayment() {
            return interestPayment;
        }
        
        public double getRemainingBalance() {
            return remainingBalance;
        }
        
        @Override
        public String toString() {
            return String.format("Payment #%d: Payment=%.2f, Principal=%.2f, Interest=%.2f, Remaining=%.2f",
                    paymentNumber, payment, principalPayment, interestPayment, remainingBalance);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mortgage mortgage = (Mortgage) o;
        return Double.compare(mortgage.principal, principal) == 0 &&
                Double.compare(mortgage.annualInterestRate, annualInterestRate) == 0 &&
                loanTermInYears == mortgage.loanTermInYears;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(principal, annualInterestRate, loanTermInYears);
    }
    
    @Override
    public String toString() {
        return String.format("Mortgage{principal=%.2f, rate=%.2f%%, term=%d years, monthly=%.2f}",
                principal, annualInterestRate, loanTermInYears, monthlyPayment);
    }
}