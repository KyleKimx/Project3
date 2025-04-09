package com.example.project3.rubank;

/**
 * The CollegeChecking class represents a college checking account.
 * It extends the Checking class and includes additional properties such as campus and no monthly fee.
 * Provides methods to calculate fees and retrieve campus information.
 *
 * Requirements:
 *  - same 1.5% annual interest as Checking
 *  - no monthly fee
 *  - campus code: 1=New Brunswick, 2=Newark, 3=Camden
 *  - must be 24 or younger to open
 * NOTE: The no-fee rule overrides the standard Checking fee logic.
 * So we override fee() to return 0, but keep the same interest() from Checking.
 *
 * We add a campus field with a Campus enum.
 *
 * @see Checking
 * @author Alison Chu, Byounguk Kim
 */
public class CollegeChecking extends Checking {
    private static final double COLLEGE_MONTHLY_FEE = 0.0;
    private Campus campus;

    /**
     * Constructs a CollegeChecking account with the specified account number, holder, balance, and campus.
     * @param number the account number
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     * @param campus the campus associated with the account
     */
    public CollegeChecking(AccountNumber number, Profile holder, double balance, Campus campus) {
        super(number, holder, balance);
        this.campus = campus;
    }

    /**
     * Returns the fee for the college checking account.
     * The fee is always 0.0 for college checking accounts.
     * @return the monthly fee, which is always 0.0
     */
    @Override
    public double fee() {
        return COLLEGE_MONTHLY_FEE;
    }

    /**
     * Returns the campus associated with the college checking account.
     * @return the campus
     */
    public Campus getCampus() {
        return campus;
    }

    /**
     * Returns a string representation of the college checking account.
     * @return a string representation of the college checking account
     */
    @Override
    public String toString() {
        return super.toString() + String.format(" Campus[%s]", campus.name());
    }
}
