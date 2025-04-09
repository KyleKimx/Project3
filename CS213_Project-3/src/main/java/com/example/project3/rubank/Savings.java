package com.example.project3.rubank;

/**
 * The Savings class represents a regular savings account.
 * It extends the Account class and includes properties such as loyalty status, interest rates, and fees.
 * Provides methods to calculate fees, interest, and retrieve the annual interest rate.
 *
 * Requirements:
 *  - 2.5% annual interest normally
 *  - If loyal, +0.25% => 2.75% annual
 *  - $25 monthly fee if balance < $500, else 0
 *  - loyal is set if the holder also has a Checking, etc. (logic in TransactionManager or after load)
 *
 * @see Account
 * @author Alison Chu, Byounguk Kim
 */
public class Savings extends Account {
    private static final double BASE_ANNUAL = 0.025;     
    private static final double LOYAL_ANNUAL = 0.0275;
    private static final double WAIVE_THRESHOLD = 500.0;
    public static final int NUMBER_MONTHS = 12;
    public static final double MONTHLY_FEE = 25.0;

    protected boolean isLoyal;

    /**
     * Constructs a Savings account with the specified account number, holder, and balance.
     * @param number the account number
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     */
    public Savings(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
        this.isLoyal = false;
    }

    /**
     * Returns the monthly interest for the savings account.
     * @return the monthly interest
     */
    @Override
    public double interest() {
        double rate = isLoyal ? LOYAL_ANNUAL : BASE_ANNUAL;
        return balance * (rate / NUMBER_MONTHS);
    }

    /**
     * Returns the fee for the savings account.
     * The fee is waived if the balance is above the threshold.
     * @return the monthly fee
     */
    @Override
    public double fee() {
        if (balance >= WAIVE_THRESHOLD) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }

    /**
     * Returns a string representation of the savings account.
     * @return a string representation of the savings account
     */
    @Override
    public String toString() {
        String loyalTag = isLoyal ? "[LOYAL]" : "";
        return super.toString() + " " + loyalTag;
    }
}
