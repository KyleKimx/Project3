package com.example.project3.util;

import com.example.project3.rubank.*;

/**
 * Utility class for sorting lists of Accounts in different ways:
 * by branch, by holder, by type, etc.
 * Methods are static and can modify a List<Account> in-place.
 * @author Alison Chu, Byounguk Kim
 */
public class Sort {

    /**
     * Sorts the accounts by branch (county, city), in-place.
     * @param list the list of accounts
     */
    public static void sortByBranch(List<Account> list) {
        int listSize = list.size();
        for (int i = 0; i < listSize - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < listSize; j++) {
                Branch b1 = list.get(j).getNumber().getBranch();
                Branch b2 = list.get(minIndex).getNumber().getBranch();
                int countyComparison = b1.getCounty().compareToIgnoreCase(b2.getCounty());
                if (countyComparison < 0) {
                    minIndex = j;
                } else if (countyComparison == 0) {
                    int cityComparison = b1.name().compareToIgnoreCase(b2.name());
                    if (cityComparison < 0) {
                        minIndex = j;
                    }
                }
            }
            if (minIndex != i) {
                Account temp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, temp);
            }
        }
    }

    /**
     * Sorts the accounts by holder (last name, first name, DOB), then by account number if needed.
     * @param list the list of accounts
     */
    public static void sortByHolder(List<Account> list) {
        int listSize = list.size();
        for (int i = 0; i < listSize - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < listSize; j++) {
                Profile p1 = list.get(j).getHolder();
                Profile p2 = list.get(minIndex).getHolder();
                int cmp = p1.compareTo(p2);
                if (cmp < 0) {
                    minIndex = j;
                } else if (cmp == 0) {
                    Account a1 = list.get(j);
                    Account a2 = list.get(minIndex);
                    if (a1.getNumber().compareTo(a2.getNumber()) < 0) {
                        minIndex = j;
                    }
                }
            }
            if (minIndex != i) {
                Account tmp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, tmp);
            }
        }
    }

    /**
     * Sorts the accounts by account type, then by account number if needed.
     * The order of AccountTypes is presumably CHECKING < SAVINGS < MONEY_MARKET < COLLEGE_CHECKING < CD,
     * or you can define the order by comparing the enum's ordinal or code.
     * @param list the list of accounts
     */
    public static void sortByType(List<Account> list) {
        int listSize = list.size();
        for (int i = 0; i < listSize - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < listSize; j++) {
                AccountType t1 = list.get(j).getNumber().getType();
                AccountType t2 = list.get(minIndex).getNumber().getType();
                int cmp = t1.compareTo(t2);
                if (cmp < 0) {
                    minIndex = j;
                } else if (cmp == 0) {
                    Account a1 = list.get(j);
                    Account a2 = list.get(minIndex);
                    if (a1.getNumber().compareTo(a2.getNumber()) < 0) {
                        minIndex = j;
                    }
                }
            }
            if (minIndex != i) {
                Account tmp = list.get(i);
                list.set(i, list.get(minIndex));
                list.set(minIndex, tmp);
            }
        }
    }

    /**
     * Sorts the accounts based on the specified key.
     * 'B' for branch, 'H' for holder, 'T' for type.
     * @param list the list of accounts
     * @param key the sorting key
     * @throws IllegalArgumentException if the key is invalid
     */
    public static void account(List<Account> list, char key) {
        switch (Character.toUpperCase(key)) {
            case 'B':
                sortByBranch(list);
                break;
            case 'H':
                sortByHolder(list);
                break;
            case 'T':
                sortByType(list);
                break;
            default:
                throw new IllegalArgumentException("Invalid sorting key: " + key);
        }
    }

    /**
     * Sorts the accounts in the AccountDatabase based on the specified key.
     * 'B' for branch, 'H' for holder, 'T' for type.
     * @param list the AccountDatabase containing the accounts
     * @param key the sorting key
     * @throws IllegalArgumentException if the key is invalid
     */
    public static void account(AccountDatabase list, char key) {

        account((List<Account>) list, key);
    }
}

