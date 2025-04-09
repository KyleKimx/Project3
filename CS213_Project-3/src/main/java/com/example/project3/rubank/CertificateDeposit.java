package com.example.project3.rubank;

import com.example.project3.util.Date;

import java.util.Calendar;

/**
 * The CertificateDeposit class represents a certificate of deposit account.
 * It extends the Savings class and includes additional properties such as term, open date, and penalty.
 * Provides methods to calculate fees, interest, and maturity date, as well as
 * to compute closing interest and penalties.
 * @author Alison Chu, Byounguk Kim
 */
public class CertificateDeposit extends Savings {
    private static final double RATE_3MO = 0.03;
    private static final double RATE_6MO = 0.0325;
    private static final double RATE_9MO = 0.035;
    private static final double RATE_12MO = 0.04;
    private static final int DAYS_PER_MONTH = 30;
    private static final int DAYS_PER_YEAR = 365;
    private static final double PENALTY_RATE = 0.10;
    private static final int MONTH_OFFSET = 1; 
    private static final double ROUNDING_FACTOR = 100.0; 
    private static final long MILLIS_PER_DAY = 1000L * 60L * 60L * 24L;

    private int term;
    private Date openDate;
    private double penalty;


    /**
     * Constructs a CertificateDeposit with the specified account number, holder, balance, term, and open date.
     * @param number the account number
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     * @param term the term of the certificate deposit in months
     * @param openDate the date the certificate deposit was opened
     */
    public CertificateDeposit(AccountNumber number, Profile holder, double balance, int term, Date openDate) {
        super(number, holder, balance);
        this.term = term;
        this.openDate = openDate;
    }

    /**
     * Calculates the number of days between two dates.
     * @param start the start date
     * @param end the end date
     * @return the number of days between the start and end dates
     */
    private int daysBetween(Date start, Date end) {
        Calendar c1 = Calendar.getInstance();
        c1.set(start.getYear(), start.getMonth() - MONTH_OFFSET, start.getDay());
        Calendar c2 = Calendar.getInstance();
        c2.set(end.getYear(), end.getMonth() - MONTH_OFFSET, end.getDay());
        long diffMillis = c2.getTimeInMillis() - c1.getTimeInMillis();
        long days = diffMillis / MILLIS_PER_DAY;
        return (int) days;
    }

    /**
     * Returns the fee for the certificate deposit account.
     * @return the fee, which is always 0.0 for certificate deposit accounts
     */
    @Override
    public double fee() {
        return 0.0;
    }

    /**
     * Returns the monthly interest for the certificate deposit account.
     * @return the monthly interest
     */
    @Override
    public double interest() {
        double annualRate = getAnnualRate();
        return balance * (annualRate / NUMBER_MONTHS);
    }

    /**
     * Returns the annual interest rate based on the term of the certificate deposit.
     * @return the annual interest rate
     */
    public double getAnnualRate() {
        switch (term) {
            case 3:  
                return RATE_3MO;
            case 6:  
                return RATE_6MO;
            case 9:  
                return RATE_9MO;
            case 12: 
                return RATE_12MO;
            default: 
                return 0.0;
        }
    }

    /**
     * Returns the maturity date of the certificate deposit.
     * @return the maturity date
     */
    public Date maturityDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(openDate.getYear(), openDate.getMonth() - 1, openDate.getDay(), 0, 0, 0);
        calendar.add(Calendar.MONTH, term);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + MONTH_OFFSET;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new Date(month + "/" + day + "/" + year);
    }

    /**
     * Computes the closing interest for the certificate deposit based on the close date.
     * Applies penalties if the account is closed before maturity.
     * @param closeDate the date the account is closed
     * @return the closing interest
     */
    public double computeClosingInterest(Date closeDate) {
        int daysBetween = daysBetween(openDate, closeDate)+1;
        int maturityDays = term * DAYS_PER_MONTH;
        double dailyRate;
        double interest;
        double computedPenalty = 0.0;
        if (daysBetween >= maturityDays) {
            dailyRate = getAnnualRate() / DAYS_PER_YEAR;
        } else {
            double months = (double) daysBetween / DAYS_PER_MONTH;
            if (months <= 6) {
                dailyRate = RATE_3MO / DAYS_PER_YEAR;
            } else if (months <= 9) {
                dailyRate = RATE_6MO / DAYS_PER_YEAR;
            } else {
                dailyRate = RATE_9MO / DAYS_PER_YEAR;
            }
            computedPenalty = PENALTY_RATE * (balance * dailyRate * daysBetween);
        }
        interest = balance * dailyRate * daysBetween;
        interest = Math.round(interest * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        computedPenalty = Math.round(computedPenalty * ROUNDING_FACTOR) / ROUNDING_FACTOR;
        this.penalty = computedPenalty;
        return interest;
    }

    /**
     * Returns the penalty for early closure of the certificate deposit.
     * @return the penalty
     */
    public double getPenalty() {
        return this.penalty;
    }

    /**
     * Returns the penalty for early closure of the certificate deposit.
     * @return the penalty
     */
    public int getTerm() {
        return term;
    }

    /**
     * Returns the open date of the certificate deposit.
     * @return the open date
     */
    public Date getOpenDate() {
        return openDate;
    }

    /**
     * Returns a string representation of the certificate deposit.
     * @return a string representation of the certificate deposit
     */
    @Override
    public String toString() {
        this.isLoyal = false;
        return super.toString()
                + String.format("Term[%d] Date opened[%s] Maturity date[%s]",
                term, openDate.toString(), maturityDate().toString());
    }
}

