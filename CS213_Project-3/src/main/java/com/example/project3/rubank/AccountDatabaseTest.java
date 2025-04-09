package com.example.project3.rubank;//package com.example.project3.rubank;
//
//import org.junit.Before;
//import org.junit.Test;
//import util.Date;
//
//import static org.junit.Assert.*;
//
///**
// * The AccountDatabaseTest class contains unit tests for the AccountDatabase class.
// * It tests the functionality of adding, depositing, and withdrawing from accounts in the database.
// * This class ensures that the AccountDatabase behaves as expected under various scenarios.
// * @see AccountDatabase
// * @see Account
// * @see AccountNumber
// * @see Profile
// * @see Date
// * @author Alison Chu, Byounguk Kim
// */
//public class AccountDatabaseTest {
//
//    private AccountDatabase accountDatabase;
//    private AccountNumber checkingAccountNumber;
//    private AccountNumber moneyMarketAccountNumber;
//    private Profile profile;
//    private Date dob;
//
//    /**
//     * Sets up the test environment by initializing the AccountDatabase and adding test accounts.
//     */
//    @Before
//    public void setUp() {
//        accountDatabase = new AccountDatabase();
//        dob = new Date("01/01/2000");
//        profile = new Profile("John", "Doe", dob);
//        checkingAccountNumber = new AccountNumber(Branch.PISCATAWAY, AccountType.CHECKING);
//        moneyMarketAccountNumber = new AccountNumber(Branch.PISCATAWAY, AccountType.MONEY_MARKET);
//
//        Account checkingAccount = new Checking(checkingAccountNumber, profile, 500);
//        Account moneyMarketAccount = new MoneyMarket(moneyMarketAccountNumber, profile, 4000);
//
//        accountDatabase.add(checkingAccount);
//        accountDatabase.add(moneyMarketAccount);
//    }
//
//    /**
//     * Tests that depositing into a checking account increases the balance correctly.
//     */
//    @Test
//    public void depositIntoCheckingAccount_shouldIncreaseBalance() {
//        accountDatabase.deposit(checkingAccountNumber, 500);
//        Account account = accountDatabase.findByNumber(checkingAccountNumber.toString());
//        assertEquals(1000, account.getBalance(), 0.01);
//    }
//
//    /**
//     * Tests that depositing into a money market account increases the balance and sets the loyalty status correctly.
//     */
//    @Test
//    public void depositIntoMoneyMarketAccount_shouldIncreaseBalanceAndSetLoyal() {
//        accountDatabase.deposit(moneyMarketAccountNumber, 2000);
//        Account account = accountDatabase.findByNumber(moneyMarketAccountNumber.toString());
//        assertEquals(6000, account.getBalance(), 0.01);
//        assertTrue(((MoneyMarket) account).isLoyal());
//
//    }
//
//    /**
//     * Tests that withdrawing from a checking account decreases the balance correctly.
//     */
//    @Test
//    public void withdrawFromCheckingAccount_shouldDecreaseBalance() {
//        boolean success = accountDatabase.withdraw(checkingAccountNumber, 200);
//        Account account = accountDatabase.findByNumber(checkingAccountNumber.toString());
//        assertTrue(success);
//        assertEquals(300, account.getBalance(), 0.01);
//    }
//
//    /**
//     * Tests that withdrawing from a checking account with insufficient funds fails.
//     */
//    @Test
//    public void withdrawFromCheckingAccount_withInsufficientFunds_shouldFail() {
//        boolean success = accountDatabase.withdraw(checkingAccountNumber, 1000);
//        Account account = accountDatabase.findByNumber(checkingAccountNumber.toString());
//        assertFalse(success);
//        assertEquals(500, account.getBalance(), 0.01);
//    }
//
//    /**
//     * Tests that withdrawing from a money market account decreases the balance and updates the loyalty status correctly.
//     */
//    @Test
//    public void withdrawFromMoneyMarketAccount_shouldDecreaseBalanceAndUpdateLoyalty() {
//        accountDatabase.deposit(moneyMarketAccountNumber, 2000); // Increase balance to >= $5,000
//        boolean success = accountDatabase.withdraw(moneyMarketAccountNumber, 1500);
//        Account account = accountDatabase.findByNumber(moneyMarketAccountNumber.toString());
//        assertTrue(success);
//        assertEquals(4500, account.getBalance(), 0.01);
//        assertFalse(((MoneyMarket) account).isLoyal());
//    }
//}