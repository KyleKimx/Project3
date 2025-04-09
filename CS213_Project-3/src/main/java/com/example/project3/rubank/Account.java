package com.example.project3.rubank;

import java.util.List;
import java.text.DecimalFormat;

/**
 * The Account class represents a bank account.
 * An account has a unique account number, a holder, and a balance.
 * An account can be compared to another account based on the account number.
 * This class is abstract and must be extended by specific account types.
 * It provides methods for depositing, withdrawing, and generating account statements.
 * It also maintains a list of activities associated with the account.
 * @author Alison Chu, Byounguk Kim
 */
public abstract class Account implements Comparable<Account> {
    private StringBuilder statementSB = new StringBuilder();
    protected AccountNumber number;
    protected Profile holder;
    protected double balance;
    protected List<Activity> activities;

    /**
     * Constructs an account with the given account number, holder, and balance.
     * @param number the account number
     * @param holder the account holder
     * @param balance the account balance
     */
    public Account(AccountNumber number, Profile holder, double balance){
        this.number = number;
        this.holder = holder;
        this.balance = balance;
        //this.activities = new List<>(); // I change the List<> to ArrayList<>() because List<> is not a class in Java
    }

    /**
     * Prints the activities associated with the account.
     * @return a string representation of the activities
     */
    private String printActivities() {
        String str = "";
        if (!activities.isEmpty()) {
            str += "\t[Activity]\n";
            for (Activity act : activities) {
                String wOrD = (act.getType() == 'D') ? "deposit" : "withdrawal";
                str += String.format("\t\t\t%s::%s%s::%s::$%,.2f\n",
                        act.getDate().toString(),
                        act.getLocation().name(),
                        act.isAtm() ? "[ATM]" : "",
                        wOrD,
                        act.getAmount());
            }
        }
        statementSB.append(str);
        return str;
    }

    /**
     * Prints the interest and fee for the account.
     * @param interest the interest amount
     * @param fee the fee amount
     * @return a string representation of the interest and fee
     */
    private String printInterestFee(double interest, double fee) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String str = String.format("\t[interest] $%s [Fee] $%s\n", df.format(interest), df.format(fee));
        statementSB.append(str);
        return str;
    }

    /**
     * Prints the balance of the account after applying interest and fee.
     * @param interest the interest amount
     * @param fee the fee amount
     * @return a string representation of the balance
     */
    private String printBalance(double interest, double fee) {
        balance = balance + interest - fee;
        DecimalFormat df = new DecimalFormat("#,##0.00");
        String str = "\t[Balance] $" + df.format(balance) + "\n";
        statementSB.append(str);
        return str;
    }

    /**
     * Builds the account statement.
     * @return the account statement as a string
     */
    public String buildStatement() {
        statementSB.setLength(0);
        this.statement();
        return statementSB.toString();
    }
    /**
     * Prints the account statement, including activities, interest, fee, and balance.
     */
    public final void statement() {
        printActivities();
        double interest = interest();
        double fee = fee();
        printInterestFee(interest, fee);
        printBalance(interest, fee);
    }

    /**
     * Adds an activity to the account.
     * @param activity the activity to add
     */
    public void addActivity(Activity activity){

        activities.add(activity);
    }

    /**
     * Calculates the interest for the account.
     * @return the interest amount
     */
    public abstract double interest();

    /**
     * Calculates the fee for the account.
     * @return the fee amount
     */
    public abstract double fee();

    /**
     * Deposits the given amount into the account.
     * @param amount the amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Withdraws the given amount from the account.
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     */
    public void withdraw(double amount) {
        if (balance < amount) {
            throw new IllegalArgumentException();
        }
        balance -= amount;
    }

    /**
     * Returns the account number of this account.
     * @return the account number
     */
    public AccountNumber getNumber(){

        return number;
    }

    /**
     * Returns the holder of this account.
     * @return the account holder
     */
    public Profile getHolder(){

        return holder;
    }

    /**
     * Returns the balance of this account.
     * @return the account balance
     */
    public double getBalance(){

        return balance;
    }

    /**
     * Returns the list of activities of this account.
     * @return the list of activities
     */
    public List<Activity> getActivities() {

        return activities;
    }

    /**
     * Compares this account with the specified account for order based on account numbers.
     * @param other the account to be compared
     * @return a negative integer, zero, or a positive integer as this account is less than, equal to,
     * or greater than the specified account.
     */
    @Override
    public int compareTo(Account other){

        return this.number.compareTo(other.number);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two accounts are equal if they have the same account number and holder.
     * @param obj the reference object with which to compare
     * @return true if this object is the same as the obj argument; false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Account other = (Account) obj;
        return this.number.equals(other.number);
    }

    /**
     * Returns a string representation of this account.
     * The string representation consists of the account number, holder, balance, and branch.
     * @return a string representation of this account
     */
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return String.format("Account#[%s] Holder[%s] Balance[$%s] Branch[%s]",
            number.toString(), 
            holder.toString(),
            df.format(balance),
            number.getBranch().name() 
        );
    }
}

