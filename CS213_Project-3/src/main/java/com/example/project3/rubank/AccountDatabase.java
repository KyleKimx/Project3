package com.example.project3.rubank;

import com.example.project3.util.Date;
import com.example.project3.util.List;
import com.example.project3.util.Sort;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The AccountDatabase class represents a database of accounts. It is responsible for storing and managing accounts.
 * It also provides methods for adding, removing, and retrieving accounts from the database.
 * The database is implemented as an array of accounts.
 * @author Alison Chu, Byounguk Kim
 */
public class AccountDatabase extends List<Account> {
    private Archive archive;

    /**
     * Constructs an empty AccountDatabase.
     */
    public AccountDatabase(){
        super();
        this.archive = new Archive();
    }

    /**
     * Updates the loyalty status of savings accounts.
     */
    private void updateLoyalty(){
        for (Account account : this) {
            if (account instanceof Savings && !(account instanceof MoneyMarket)) {
                boolean hasChecking = false;
                for (int j = 0; j < size(); j++) {
                    Account other = get(j);
                    if (other.getHolder().equals(account.getHolder()) &&
                            other.getNumber().getType() == AccountType.CHECKING) {
                        hasChecking = true;
                        break;
                    }
                }
                ((Savings) account).isLoyal = hasChecking;
            }
        }
    }

    /**
     * Loads accounts from the given file.
     * @param file the file to load accounts from
     * @return the number of accounts loaded
     * @throws IOException if an I/O error occurs
     */
    public void loadAccounts(File file) throws IOException {
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split(",");
            AccountType type = AccountType.fromString(tokens[0]);
            double initBal = Double.parseDouble(tokens[5]);
            //Profile holder = new Profile(tokens[2], tokens[3], new Date(tokens[4]));
//            AccountNumber acctNum = new AccountNumber(parseBranch(tokens[1]), type);
//            Account newAcct = null;
//            switch (type) {
//                case CHECKING:
//                    newAcct = new Checking(acctNum, holder, initBal);
//                    break;
//                case SAVINGS:
//                    newAcct = new Savings(acctNum, holder, initBal);
//                    break;
//                case MONEY_MARKET:
//                    MoneyMarket mmNewAcct = new MoneyMarket(acctNum, holder, initBal);
//                    mmNewAcct.isLoyal = (initBal >= 5000);
//                    newAcct = mmNewAcct;
//                    break;
//                case COLLEGE_CHECKING:
//                    int campusCode = Integer.parseInt(tokens[6]);
//                    Campus campus = Campus.fromInt(campusCode);
//                    newAcct = new CollegeChecking(acctNum, holder, initBal, campus);
//                    break;
//                case CD:
//                    int term = Integer.parseInt(tokens[6]);
//                    Date cdOpenDate = new Date(tokens[7]);
//                    newAcct = new CertificateDeposit(acctNum, holder, initBal, term, cdOpenDate);
//                    break;
//                default: continue;
//            }
//            add(newAcct);
        }
        sc.close();
        updateLoyalty();
    }

    /**
     * Adds an account to the database and updates loyalty status if necessary.
     * @param account the account to add
     */
    @Override
    public void add(Account account) {
        super.add(account);
        if (account.getNumber().getType() == AccountType.CHECKING || account.getNumber().getType() == AccountType.SAVINGS) {
            updateLoyalty();
        }
    }


    /**
     * Removes an account from the database and updates loyalty status if necessary.
     * @param account the account to remove
     */
    @Override
    public void remove(Account account) {
        super.remove(account);
        if (account.getNumber().getType() == AccountType.CHECKING) {
            updateLoyalty();
        }
    }


    /**
     * Processes activities from the given file.
     * @param file the file to process activities from
     * @return the number of activities processed
     * @throws IOException if an I/O error occurs
     */
    public void processActivities(File file) throws IOException {
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split(",");
            if (tokens.length < 5) {
                continue;
            }
            char depositOrWithdrawal = tokens[0].charAt(0);
            String acctNumString = tokens[1];
            Date actDate = new Date(tokens[2]);
            Branch loc = parseBranch(tokens[3]);
            double amt = Double.parseDouble(tokens[4]);
            Account target = findByNumber(acctNumString);
            if (target == null) {
                continue;
            }
            if (depositOrWithdrawal == 'D') {
                target.deposit(amt);
            } else {
                target.withdraw(amt);
            }
            //Activity activity = new Activity(actDate, loc, depositOrWithdrawal, amt, true);
            //target.addActivity(activity);
        }
        sc.close();
    }

    /**
     * Withdraws the given amount from the account with the specified account number.
     * @param number the account number
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdraw(AccountNumber number, double amount) {
        Account account = findByNumber(number.toString());
        if (account == null) {
            return false;
        }
        try {
            account.withdraw(amount);
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Deposits the given amount into the account with the specified account number.
     * @param number the account number
     * @param amount the amount to deposit
     */
    public void deposit(AccountNumber number, double amount) {
        Account account = findByNumber(number.toString());
        if (account == null) {
            throw new IllegalArgumentException();
        }
        account.deposit(amount);
    }

    /**
     * Finds an account by its account number.
     * @param acctNumString the account number as a string
     * @return the account with the specified account number, or null if no such account exists
     */
    public Account findByNumber(String acctNumString){
        for (Account account : this) {
            if (account.getNumber().toString().equals(acctNumString)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Returns the archive of closed accounts.
     * @return the archive
     */
    public Archive getArchive() {

        return archive;
    }

    /**
     * Closes the specified account and adds it to the archive.
     * @param account the account to close
     * @param closeDate the date the account was closed
     */
    public void closeAccount(Account account, Date closeDate){
        remove(account);
        archive.add(account, closeDate);
    }

    /**
     * Checks if the specified account holder has a duplicate account of the specified type.
     * @param holder the account holder
     * @param type the account type
     * @return true if the holder has a duplicate account of the specified type, false otherwise
     */
    public boolean hasDuplicateType(Profile holder, AccountType type) {
        for (Account account : this) {
            if (account.getHolder().equals(holder) && account.getNumber().getType() == type) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses a branch from the given input string.
     * @param input the input string
     * @return the parsed branch, or null if no matching branch is found
     */
    public Branch parseBranch(String input){
        for (Branch branch : Branch.values()) {
            if (branch.name().equalsIgnoreCase(input)) {
                return branch;
            }
        }
        return null;
    }

    /**
     * Prints the archive of closed accounts.
     */
    public void printArchive(){
        if (archive.isEmpty()) {
            System.out.println("Archive is empty.");
        } else {
            System.out.println("\n*List of closed accounts in the archive.");
            archive.print();
            System.out.println("*end of list.\n");
        }
    }

    /**
     * Prints the statements of all accounts, sorted by account holder.
     */
    public void printStatements(){
        Sort.account(this, 'H');
        System.out.println("*Account statements by account holder.");
        Profile current = null;
        int countHolder = 0;
        for (Account acct : this) {
            Profile profile = acct.getHolder();
            if (current == null || !current.equals(profile)) {
                current = profile;
                countHolder++;
                System.out.printf("%d.%s\n", countHolder, current.toString());
            }
            System.out.println("\t[Account#] " + acct.getNumber());
            String statementStr = acct.buildStatement();
            System.out.print(statementStr);
            System.out.println();
        }
        System.out.println("*end of statements.\n");
    }

    /**
     * Prints the statements of all accounts, sorted by account holder.
     */
    public void printByBranch(){
        if (isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by branch location (county, city).");
        Sort.account(this, 'B');
        String currentCounty = null;
        for (Account account : this) {
            Branch branch = account.getNumber().getBranch();
            String cty = branch.getCounty();
            if (currentCounty == null || !cty.equalsIgnoreCase(currentCounty)) {
                currentCounty = cty;
                System.out.println("County: " + cty);
            }
            System.out.println(account);
        }
        System.out.println("*end of list.\n");
    }

    /**
     * Prints the list of accounts ordered by account holder and number.
     */
    public void printByHolder(){
        if (isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by account holder and number.");
        Sort.account(this, 'H');
        for (Account account : this) {
            System.out.println(account);
        }
        System.out.println("*end of list.\n");
    }


    /**
     * Prints the list of accounts ordered by account type and number.
     */
    public void printByType(){
        if (isEmpty()) {
            System.out.println("Account database is empty!");
            return;
        }
        System.out.println("\n*List of accounts ordered by account type and number.");
        Sort.account(this, 'T');
        AccountType currentType = null;
        for (Account account : this) {
            AccountType type = account.getNumber().getType();
            if (currentType == null || type != currentType) {
                currentType = type;
                System.out.println("Account Type: " + type.name());
            }
            System.out.println(account);
        }
        System.out.println("*end of list.\n");
    }

    /**
     * Opens a new account if it doesn't already exist.
     * @param account the account to open
     * @return true if the account was successfully added, false if it already exists
     */
    public boolean open(Account account) {
        for (Account existing : this) {
            if (existing.equals(account)) {
                return false; // Already exists
            }
        }
        add(account);
        return true;
    }







}


