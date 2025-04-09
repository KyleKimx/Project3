package com.example.project3.rubank;

/**
 * The Checking class represents a checking account.
 * It extends the Account class and includes additional properties such as annual interest, monthly fee, and waive threshold.
 * Provides methods to calculate fees and interest.
 * @author Alison Chu, Byounguk Kim
 */
public class Checking extends Account {
    private static final double ANNUAL_INTEREST = 0.015; 
    private static final double MONTHLY_FEE = 15.0;
    private static final double WAIVE_THRESHOLD = 1000.0;
    public static final int NUMBER_MONTHS = 12;

    /**
     * Constructs a Checking account with the specified account number, holder, and balance.
     * @param number the account number
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     */
    public Checking(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
    }

    /**
     * Returns the monthly interest for the checking account.
     * @return the monthly interest
     */
    @Override
    public double interest() {
        return balance * (ANNUAL_INTEREST / NUMBER_MONTHS);
    }

    /**
     * Returns the monthly interest for the checking account.
     * @return the monthly interest
     */
    @Override
    public double fee() {
        if (balance >= WAIVE_THRESHOLD) {
            return 0.0;
        }
        return MONTHLY_FEE;
    }
}

