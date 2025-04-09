package com.example.project3.rubank;

/**
 * The MoneyMarket class represents a money market account.
 * It extends the Savings class and includes additional properties such as withdrawal count, interest rates, and fees.
 * Provides methods to calculate fees, interest, and handle withdrawals.
 *
 * Requirements:
 *  - 3.5% annual interest, +0.25% if loyal => 3.75%
 *  - $25 monthly fee if balance < $2000
 *  - up to 3 free withdrawals per statement cycle; if >3, add $10 fee
 *  - 'withdrawal' field tracks how many times we've withdrawn in the cycle
 *  - no downgrading to regular savings in Project 2
 *
 * @see Savings
 * @author Alison Chu, Byounguk Kim
 */
public class MoneyMarket extends Savings {
    private static final double BASE_ANNUAL = 0.035;
    private static final double LOYAL_ANNUAL = 0.0375;
    private static final double BALANCE_THRESHOLD = 2000.0;
    private static final int FREE_WITHDRAWALS = 3;
    private static final double EXTRA_WITHDRAW_FEE = 10.0;
    private static final double LOYALTY_REQ = 5000.0;
    private int withdrawal;

    /**
     * Constructs a MoneyMarket account with the specified account number, holder, and balance.
     * @param number the account number
     * @param holder the profile of the account holder
     * @param balance the initial balance of the account
     */
    public MoneyMarket(AccountNumber number, Profile holder, double balance) {
        super(number, holder, balance);
        this.withdrawal = 0;
    }

    public boolean isLoyal() {
        return this.isLoyal;
    }
    /**
     * Withdraws the specified amount from the money market account.
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if the balance is insufficient
     */
    @Override
    public void withdraw(double amount) {
        super.withdraw(amount);
        withdrawal++;
        if (balance < LOYALTY_REQ) {
            isLoyal = false;
        }
    }

    /**
     * Deposits the specified amount into the money market account.
     * @param amount the amount to deposit
     */
    @Override
    public void deposit(double amount) {
        super.deposit(amount);
        isLoyal = (getBalance() >= LOYALTY_REQ);
    }

    /**
     * Returns the monthly interest for the money market account.
     * @return the monthly interest
     */
     @Override
     public double interest() {
         double rate = isLoyal ? LOYAL_ANNUAL : BASE_ANNUAL;
         return balance * (rate / NUMBER_MONTHS);
     }

    /**
     * Returns the fee for the money market account.
     * The fee includes a base monthly fee if the balance is below the threshold and an extra fee for excess withdrawals.
     * @return the monthly fee
     */
    @Override
    public double fee() {
        double fee = 0.0;
        if (balance < BALANCE_THRESHOLD) {
            fee += MONTHLY_FEE;
        }
        if (withdrawal > FREE_WITHDRAWALS) {
            fee += EXTRA_WITHDRAW_FEE;
        }
        return fee;
    }

    /**
     * Returns a string representation of the money market account.
     * @return a string representation of the money market account
     */
    @Override
    public String toString() {

        return super.toString() + String.format(" Withdrawal[%d]", withdrawal);
    }
}
