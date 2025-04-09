package com.example.project3.rubank;


import com.example.project3.util.Date;

import java.text.DecimalFormat;

/**
 * The Activity class represents a transaction activity for a bank account.
 * An activity has a date, location, type (deposit or withdrawal), amount, and a flag indicating if it was an ATM transaction.
 * Activities can be compared based on the date of the transaction.
 * The string representation of an activity includes the date, location, type, and amount formatted appropriately.
 * This class implements the Comparable interface to allow sorting by date.
 * @see Date
 * @see Branch
 * @see Comparable
 * @author Alison Chu, Byounguk Kim
 */
public class Activity implements Comparable<Activity> {
    private Date date;
    private Branch location;
    private char type;
    private double amount;
    private boolean atm;

    /**
     * Constructs an Activity with the specified date, location, type, amount, and ATM flag.
     * @param date the date of the activity
     * @param location the location of the activity
     * @param type the type of the activity (deposit or withdrawal)
     * @param amount the amount of the activity
     * @param atm true if the activity was an ATM transaction, false otherwise
     */
    public Activity(Date date, Branch location, char type, double amount, boolean atm) {
        this.date = date;
        this.location = location;
        this.type = type;
        this.amount = amount;
        this.atm = atm;
    }

    /**
     * Returns the date of the activity.
     * @return the date of the activity
     */
    public Date getDate() {

        return date;
    }

    /**
     * Returns the location of the activity.
     * @return the location of the activity
     */
    public Branch getLocation() {

        return location;
    }

    /**
     * Returns the type of the activity.
     * @return the type of the activity
     */
    public char getType() {

        return type;
    }

    /**
     * Returns the amount of the activity.
     * @return the amount of the activity
     */
    public double getAmount() {

        return amount;
    }

    /**
     * Returns whether the activity was an ATM transaction.
     * @return true if the activity was an ATM transaction, false otherwise
     */
    public boolean isAtm() {

        return atm;
    }

    /**
     * Compares this activity with the specified activity for order based on the date.
     * @param other the other activity to compare
     * @return a negative integer, zero, or a positive integer as this activity is less than, equal to,
     * or greater than the specified activity
     */
    @Override
    public int compareTo(Activity other) {

        return this.date.compareTo(other.date);
    }

    /**
     * Returns a string representation of the activity.
     * @return a string representation of the activity
     */
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        StringBuilder sb = new StringBuilder();
        sb.append(date.toString());
        sb.append("::");
        sb.append(location.name());
        if (atm) {
            sb.append("[ATM]");
        }
        sb.append("::");
        if (type == 'D') {
            sb.append("deposit::$");
        } else {
            sb.append("withdrawal::$");
        }
        sb.append(df.format(amount));
        return sb.toString();
    }

}