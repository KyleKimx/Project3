package com.example.project3.rubank;

import com.example.project3.util.Date;
import com.example.project3.util.List;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * The TransactionManager class manages transactions for accounts in the AccountDatabase.
 * It provides methods to open, close, deposit, and withdraw from accounts.
 * It also provides methods to print accounts in various orders.
 * This class handles the logic for managing different types of accounts, ensuring that
 * transactions are processed correctly and accounts are updated accordingly.
 * @see AccountDatabase
 * @see Account
 * @see Profile
 * @see Date
 * @see List
 * @see Activity
 * @see AccountType
 * @see Branch
 * @author Alison Chu, Byounguk Kim
 */
public class TransactionManager {
    private final AccountDatabase accountDatabase;

    /**
     * Constructs a TransactionManager with an empty AccountDatabase.
     */
    public TransactionManager() {

        accountDatabase = new AccountDatabase();
    }

    /**
     * Opens an account based on the provided tokens.
     * @param tokens the tokens containing account information
     */
    private void openAccount(String[] tokens) {
        if (tokens.length < 6) {
            System.out.println("Missing data for opening an account.");
            return;
        }
        String typeInput = tokens[1].toLowerCase();
        AccountType acctType = parseAccountType(typeInput);
        String fname = tokens[3], lname = tokens[4];
        Branch branch = parseBranch(tokens[2]);
        Date dob = parseDate(tokens[5]);
        if (acctType == null || branch == null || dob == null) {
            return;
        }
        Profile holder = new Profile(fname, lname, dob);
        OpenAccountData data = new OpenAccountData();
        boolean valid = false;
        if (acctType == AccountType.CHECKING || acctType == AccountType.SAVINGS ||
                acctType == AccountType.MONEY_MARKET) {
            valid = processBasicAccount(tokens, acctType, data);
        } else if (acctType == AccountType.COLLEGE_CHECKING) {
            valid = processCollegeChecking(tokens, dob, data);
        } else if (acctType == AccountType.CD) {
            valid = processCDAccount(tokens, data);
        } else {
            System.out.println(typeInput + " - invalid account type.");
            return;
        }
        if (!valid) {
            return;
        }
        if (acctType != AccountType.CD && accountDatabase.hasDuplicateType(holder, acctType)) {
            System.out.println(fname + " " + lname + " already has a " + acctType.name() + " account.");
            return;
        }
        Account newAcct = createAccount(holder, branch, acctType, data);
        accountDatabase.add(newAcct);
        System.out.printf("%s account %s has been opened.\n", acctType.name(),
                newAcct.getNumber().toString());
    }

    /**
     * Creates an account based on the provided holder, branch, account type, and data.
     * @param holder the profile of the account holder
     * @param branch the branch of the account
     * @param acctType the type of the account
     * @param data the data required to open the account
     * @return the created account
     */
    private Account createAccount(Profile holder, Branch branch, AccountType acctType, OpenAccountData data) {
        if (acctType == AccountType.CHECKING || acctType == AccountType.SAVINGS ||
                acctType == AccountType.MONEY_MARKET) {
            return createBasicAccount(acctType, branch, holder, data.initDep);
        } else if (acctType == AccountType.COLLEGE_CHECKING) {
            return new CollegeChecking(new AccountNumber(branch, AccountType.COLLEGE_CHECKING),
                    holder, data.initDep, Campus.fromInt(data.campusCode));
        } else {
            return new CertificateDeposit(new AccountNumber(branch, AccountType.CD),
                    holder, data.initDep, data.term, data.cdOpenDate);
        }
    }

    /**
     * A helper class to store data required for opening an account.
     */
    private static class OpenAccountData {
        double initDep;
        int term;
        Date cdOpenDate;
        int campusCode;
    }

    /**
     * Processes the data required to open a basic account.
     * Validates the initial deposit.
     * @param tokens the tokens containing account information
     * @param acctType the type of the account
     * @param data the data required to open the account
     * @return true if the data is valid and the account can be opened, false otherwise
     */
    private boolean processBasicAccount(String[] tokens, AccountType acctType, OpenAccountData data) {
        if (tokens.length < 7) {
            System.out.println("Missing data tokens for opening an account.");
            return false;
        }
        data.initDep = parseDeposit(tokens[6], acctType);
        return data.initDep >= 0;
    }

    /**
     * Processes the data required to open a College Checking account.
     * Validates the initial deposit, date of birth, and campus code.
     * @param tokens the tokens containing account information
     * @param dob the date of birth of the account holder
     * @param data the data required to open the account
     * @return true if the data is valid and the account can be opened, false otherwise
     */
    private boolean processCollegeChecking(String[] tokens, Date dob, OpenAccountData data) {
        if (tokens.length < 8) {
            System.out.println("Missing data tokens for opening an account.");
            return false;
        }
        if (dob.checkCollegeAge() > 24) {
            System.out.println("Not eligible to open: " + dob + " over 24.");
            return false;
        }
        data.initDep = parseDeposit(tokens[6], AccountType.CHECKING);
        if (data.initDep < 0) {
            return false;
        }
        try {
            data.campusCode = Integer.parseInt(tokens[7]);
        } catch (NumberFormatException e) {
            System.out.println(tokens[7] + " - invalid campus code.");
            return false;
        }
        if (Campus.fromInt(data.campusCode) == null) {
            System.out.println(data.campusCode + " is not a valid campus code (1,2,3).");
            return false;
        }
        return true;
    }

    /**
     * Processes the data required to open a Certificate Deposit (CD) account.
     * Validates the initial deposit, term, and open date.
     * @param tokens the tokens containing account information
     * @param data the data required to open the account
     * @return true if the data is valid and the account can be opened, false otherwise
     */
    private boolean processCDAccount(String[] tokens, OpenAccountData data) {
        if (tokens.length < 9) {
            System.out.println("Missing deposit, term, or open date for certificate deposit.");
            return false;
        }
        data.initDep = parseDeposit(tokens[6], AccountType.CD);
        if (data.initDep < 0) {
            return false;
        }
        try {
            data.term = Integer.parseInt(tokens[7]);
        } catch (NumberFormatException e) {
            System.out.println(tokens[7] + " - invalid term (3,6,9,12).");
            return false;
        }
        data.cdOpenDate = parseOpenDate(tokens[8]);
        if (data.cdOpenDate == null) {
            return false;
        }
        if (data.term != 3 && data.term != 6 && data.term != 9 && data.term != 12) {
            System.out.println(data.term + " is not a valid term.");
            return false;
        }
        return true;
    }

    /**
     * Closes an account based on the provided tokens.
     * @param tokens the tokens containing account information
     */
    private void closeAccount(String[] tokens) {
        if (tokens.length < 2) {
            System.out.println("Missing data for closing an account.");
            return;
        }
        Date closeDate;
        try {
            closeDate = new Date(tokens[1]);
            if (!closeDate.isValid()) {
                System.out.println("Close date invalid: " + tokens[1]);
                return;
            }
        } catch (Exception e) {
            System.out.println("Close date invalid: " + tokens[1]);
            return;
        }
        if (tokens.length == 3) {
            String acctNumString = tokens[2];
            Account account = accountDatabase.findByNumber(acctNumString);
            if (account == null) {
                System.out.println(acctNumString + " account does not exist.");
                return;
            }
            closeSingleAccount(account, closeDate);
        } else if (tokens.length == 5) {
            String strDob = tokens[4];
            Date dob;
            try {
                dob = new Date(strDob);
                if (!dob.isValid()) {
                    System.out.println("DOB invalid: " + strDob + " not a valid calendar date!");
                    return;
                }
            } catch (Exception e) {
                System.out.println("DOB invalid: " + strDob);
                return;
            }
            Profile profile = new Profile(tokens[2], tokens[3], dob);
            closeAccountsForHolder(profile, closeDate);
        } else {
            System.out.println("Missing data for closing an account.");
        }
    }

    /**
     * Closes all accounts for a given holder.
     * @param holder the profile of the account holder
     * @param closeDate the date of account closure
     */
    private void closeAccountsForHolder(Profile holder, Date closeDate) {
        boolean closedAny = false;
        for (int i = 0; i < accountDatabase.size() ; i++) {
            Account candidate = accountDatabase.get(i);
            if (candidate.getHolder().equals(holder)) {
                if (!closedAny) {
                    System.out.println("Closing accounts for " + holder);
                }
                closedAny = true;
                double interestEarned = 0.0;
                double penalty = 0.0;
                if (candidate instanceof CertificateDeposit) {
                    CertificateDeposit cd = (CertificateDeposit) candidate;
                    interestEarned = cd.computeClosingInterest(closeDate);
                    penalty = cd.getPenalty();
                } else {
                    interestEarned = computePartialMonthInterest(candidate, closeDate);
                }
                System.out.printf("--%s interest earned: $%.2f\n",
                        candidate.getNumber(), interestEarned);
                if (penalty > 0) {
                    System.out.printf("  [penalty] $%.2f\n", penalty);
                }
                accountDatabase.closeAccount(candidate, closeDate);
                i--;
            }
        }
        if (closedAny) {
            System.out.println("All accounts for " + holder + " are closed and moved to archive.");
        } else {
            System.out.println(holder + " does not have any accounts in the database.");
        }
    }

    /**
     * Closes a single account.
     * @param acct the account to be closed
     * @param closeDate the date of account closure
     */
    private void closeSingleAccount(Account acct, Date closeDate) {
        System.out.println("Closing account " + acct.getNumber());
        double interestEarned = 0.0;
        double penalty = 0.0;
        if (acct instanceof CertificateDeposit) {
            CertificateDeposit cd = (CertificateDeposit) acct;
            interestEarned = cd.computeClosingInterest(closeDate);
            penalty = cd.getPenalty();
        } else {
            interestEarned = computePartialMonthInterest(acct, closeDate);
        }
        System.out.printf("--interest earned: $%.2f\n", interestEarned);
        if (penalty > 0) {
            System.out.printf("--penalty $%.2f\n", penalty);
        }
        accountDatabase.closeAccount(acct, closeDate);
    }

    /**
     * Computes the partial month interest for an account.
     * @param acct the account
     * @param closeDate the date of account closure
     * @return the computed interest
     */
    private double computePartialMonthInterest(Account acct, Date closeDate) {
        double annualRate;
        if (acct instanceof Checking) {
            annualRate = 0.015;
        } else if (acct instanceof CollegeChecking) {
            annualRate = 0.015;
        } else if (acct instanceof MoneyMarket) {
            MoneyMarket mm = (MoneyMarket) acct;
            annualRate = mm.isLoyal ? 0.0375 : 0.035;
        } else if (acct instanceof Savings) {
            Savings s = (Savings) acct;
            annualRate = s.isLoyal ? 0.0275 : 0.025;
        } else {
            annualRate = 0.0;
        }
        int dayOfMonth = closeDate.getDay();
        double dailyRate = annualRate / 365.0;
        return acct.getBalance() * dailyRate * dayOfMonth;
    }

    /**
     * Processes account activities from a file.
     */
    private void processActivities() {
        if (accountDatabase.isEmpty()) {
            System.out.println("ERROR: Account database is empty! Ensure accounts are loaded before processing activities.");
            return;
        }
        File f = new File("activities.txt");
        try {
            System.out.println("Processing \"" + f.getName() + "\"...");
            accountDatabase.processActivities(f);
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] tokens = line.split(",");
                if (tokens.length < 5) {
                    continue;
                }
                String acctNumString = tokens[1];
                Date actDate = new Date(tokens[2]);
                Branch loc = accountDatabase.parseBranch(tokens[3]);
                char depositOrWithdrawal = tokens[0].charAt(0);
                double amt = Double.parseDouble(tokens[4]);
                Activity activity = new Activity(actDate, loc, depositOrWithdrawal, amt, true);
                System.out.println(acctNumString + "::" + activity.toString());
            }
            sc.close();
            System.out.println("Account activities in \"" + f.getName() + "\" processed.");
        } catch (IOException e) {
            System.out.println("Error processing activities file: " + e.getMessage());
        }
    }

    /**
     * Deposits an amount into an account based on the provided tokens.
     * @param tokens the tokens containing account information
     */
    private void depositAccount(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Missing data tokens for the deposit.");
            return;
        }
        String accountNumberString = tokens[1];
        double amount;
        try {
            amount = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + tokens[2] + "\" - not a valid amount.");
            return;
        }
        if (amount <= 0) {
            System.out.println(amount + " - deposit amount cannot be 0 or negative.");
            return;
        }
        Account account = accountDatabase.findByNumber(accountNumberString);
        if (account == null) {
            System.out.println(accountNumberString + " does not exist.");
            return;
        }
        accountDatabase.deposit(account.getNumber(), amount);
        account.addActivity(new Activity(new Date(), account.getNumber().getBranch(), 'D', amount, false));
        DecimalFormat df = new DecimalFormat("#,##0.00");
        System.out.println("$" + df.format(amount) + " deposited to " + accountNumberString);
    }

    /**
     * Withdraws an amount from an account based on the provided tokens.
     * @param tokens the tokens containing account information
     */
    private void withdrawAccount(String[] tokens) {
        if (tokens.length < 3) {
            System.out.println("Missing data tokens for the withdrawal.");
            return;
        }
        String accountNumberString = tokens[1];
        double amount;
        try {
            amount = Double.parseDouble(tokens[2]);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + tokens[2] + "\" - not a valid amount.");
            return;
        }
        if (amount <= 0) {
            System.out.println(amount + " withdrawal amount cannot be 0 or negative.");
            return;
        }
        Account a = accountDatabase.findByNumber(accountNumberString);
        if (a == null) {
            System.out.println(accountNumberString + " does not exist.");
            return;
        }
        boolean isMoneyMarket = (a instanceof MoneyMarket);
        boolean success = accountDatabase.withdraw(a.getNumber(), amount);
        if (!success) {
            if (isMoneyMarket && a.getBalance() < 2000) {
                System.out.printf("%s balance below $2,000 - withdrawing $%,.2f - insufficient funds.\n",
                        accountNumberString, amount);
            } else {
                System.out.println(accountNumberString + " - insufficient funds.");
            }
        } else {
            a.addActivity(new Activity(new Date(), a.getNumber().getBranch(), 'W', amount, false));
            if (isMoneyMarket && a.getBalance() < 2000) {
                System.out.printf("%s balance below $2,000 - $%,.2f withdrawn from %s\n",
                        accountNumberString, amount, accountNumberString);
            } else {
                System.out.printf("$%,.2f withdrawn from %s\n", amount, accountNumberString);
            }
        }
    }

    /**
     * Parses the account type from the provided input.
     * @param input the input string
     * @return the parsed account type
     */
    private AccountType parseAccountType(String input) {
        String normalized = input.trim().toLowerCase();
        switch (normalized) {
            case "moneymarket":
                normalized = "money_market";
                break;
            case "college":
                normalized = "college_checking";
                break;
            case "certificate":
                normalized = "cd";
                break;
            default:
        }
        try {
            return AccountType.valueOf(normalized.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(input + " - invalid account type.");
            return null;
        }
    }

    /**
     * Parses the branch from the provided input.
     * @param input the input string
     * @return the parsed branch
     */
    private Branch parseBranch(String input) {
        for (Branch b : Branch.values()) {
            if (b.name().equalsIgnoreCase(input)) {
                return b;
            }
        }
        System.out.println(input + " - invalid branch.");
        return null;
    }

    /**
     * Parses the date from the provided input.
     * @param input the input string
     * @return the parsed date
     */
    private Date parseDate(String input) {
        try {
            Date dob = new Date(input);
            if (!dob.isValid()) {
                System.out.println("DOB invalid: " + input + " not a valid calendar date!");
                return null;
            }
            if (dob.isFutureDate()) {
                System.out.println("DOB invalid: " + input + " cannot be today or a future day.");
                return null;
            }
            if (!dob.isAdult()) {
                System.out.println("Not eligible to open: " + input + " under 18.");
                return null;
            }
            return dob;
        } catch (IllegalArgumentException e) {
            System.out.println("DOB invalid: " + input + " not a valid calendar date!");
            return null;
        }
    }

    /**
     * Parses the date without age check from the provided input.
     * @param input the input string
     * @return the parsed date
     */
    private Date parseOpenDate(String input) {
        try {
            Date d = new Date(input);
            if (!d.isValid()) {
                System.out.println("Date invalid: " + input + " not a valid calendar date!");
                return null;
            }
            if (d.isFutureDate()) {
                System.out.println("Date invalid: " + input + " cannot be today or a future day.");
            }
            return d;
        } catch (IllegalArgumentException e) {
            System.out.println("Date invalid: " + input + " not a valid calendar date!");
            return null;
        }
    }

    /**
     * Creates a basic account based on the account type, branch, holder, and initial deposit.
     * @param acctType the type of the account
     * @param branch the branch of the account
     * @param holder the profile of the account holder
     * @param initDeposit the initial deposit amount
     * @return the created account
     */
    private Account createBasicAccount(AccountType acctType, Branch branch, Profile holder, double initDeposit) {
        AccountNumber num = new AccountNumber(branch, acctType);
        switch (acctType) {
            case CHECKING:
                return new Checking(num, holder, initDeposit);
            case SAVINGS:
                Savings sAcct = new Savings(num, holder, initDeposit);
                return sAcct;
            case MONEY_MARKET:
                MoneyMarket mmAcct = new MoneyMarket(num, holder, initDeposit);
                mmAcct.isLoyal = (initDeposit >= 5000);
                return mmAcct;
            default:
                return null;
        }
    }

    /**
     * Parses the deposit amount from the provided input.
     * @param input the input string
     * @param accountType the type of the account
     * @return the parsed deposit amount
     */
    private double parseDeposit(String input, AccountType accountType) {
        double deposit;
        try {
            deposit = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("For input string: \"" + input + "\" - not a valid amount.");
            return -1;
        }
        if (deposit <= 0) {
            System.out.println("Initial deposit cannot be 0 or negative.");
            return -1;
        }
        if (accountType == AccountType.MONEY_MARKET && deposit < 2000) {
            System.out.println("Minimum of $2,000 to open a Money Market account.");
            return -1;
        }
        if (accountType == AccountType.CD && deposit < 1000) {
            System.out.println("Minimum of $1,000 to open a Certificate Deposit account.");
            return -1;
        }
        return deposit;
    }

    /**
     * Prints the archive of closed accounts.
     */
    private void printArchive() {

        accountDatabase.printArchive();
    }

    /**
     * Prints accounts sorted by branch.
     */
    private void printByBranch() {

        accountDatabase.printByBranch();
    }

    /**
     * Prints accounts sorted by holder.
     */
    private void printByHolder() {

        accountDatabase.printByHolder();
    }

    /**
     * Prints accounts sorted by type.
     */
    private void printByType() {

        accountDatabase.printByType();
    }

    /**
     * Prints account statements.
     */
    private void printStatements() {

        accountDatabase.printStatements();
    }

    /**
     * Runs the TransactionManager, processing commands from the console.
     */
    public void run() {
        try {
            accountDatabase.loadAccounts(new File("accounts.txt"));
            System.out.println("Accounts in \"accounts.txt\" loaded to the database.");
        } catch (IOException e) {
            System.out.println("Could not load accounts from \"accounts.txt\": " + e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transaction Manager is running.\n");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] tokens = line.split("\\s+");
            String command = tokens[0];
            if (command.equals("Q") && tokens.length == 1) {
                System.out.println("Transaction Manager is terminated.");
                break;
            } else if (command.equals("O")){
                openAccount(tokens);
            } else if (command.equals("C")) {
                closeAccount(tokens);
            } else if (command.equals("D")) {
                depositAccount(tokens);
            } else if (command.equals("W")) {
                withdrawAccount(tokens);
            } else if (command.equals("A") && tokens.length == 1) {
                processActivities();
            } else if (command.equals("P")) {
                System.out.println("P command is deprecated!");
            } else if (command.equals("PA") && tokens.length == 1) {
                printArchive();
            } else if (command.equals("PB") && tokens.length == 1) {
                printByBranch();
            } else if (command.equals("PH") && tokens.length == 1) {
                printByHolder();
            } else if (command.equals("PT") && tokens.length == 1) {
                printByType();
            } else if (command.equals("PS") && tokens.length == 1) {
                printStatements();
            } else {
                System.out.println("Invalid command!");
            }
        }
        scanner.close();
    }
}
