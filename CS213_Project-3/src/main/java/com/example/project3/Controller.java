package com.example.project3;

import com.example.project3.rubank.*;
import com.example.project3.util.Date;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ToggleGroup;
import java.time.LocalDate;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class Controller {

    private AccountDatabase database = new AccountDatabase();

    // A comboBox to list all opened account for close/deposit/withdraw
    @FXML private ComboBox<Account> accountSelectBox;

    @FXML private TextField amountField;
    @FXML private TextArea accountDisplayArea;

    @FXML private ComboBox<Integer> cdTermBox;
    @FXML private DatePicker cdDatePicker;


    @FXML private ComboBox<String> branchBox;


    // Personal Info
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;

    //Initial Deposit
    @FXML private TextField depositField;

    // Account Type
    @FXML private RadioButton checkingBtn;
    @FXML private RadioButton collegeCheckingBtn;
    @FXML private RadioButton savingsBtn;
    @FXML private RadioButton moneyMarketBtn;
    @FXML private RadioButton cdBtn;



    private ToggleGroup branchGroup = new ToggleGroup();


    // Campus Options (for college checking)
    @FXML private RadioButton newBrunswickBtn;
    @FXML private RadioButton newarkBtn;
    @FXML private RadioButton camdenBtn;

    // Output
    @FXML private TextArea outputArea;
    @FXML private TextField bottomInputField;

    // Groups for toggle functionality
    private ToggleGroup accountTypeGroup = new ToggleGroup();
    private ToggleGroup campusGroup = new ToggleGroup();

    // Called after FXML is loaded
    @FXML
    public void initialize() {
        // Group account type radio buttons
        checkingBtn.setToggleGroup(accountTypeGroup);
        collegeCheckingBtn.setToggleGroup(accountTypeGroup);
        savingsBtn.setToggleGroup(accountTypeGroup);
        moneyMarketBtn.setToggleGroup(accountTypeGroup);
        cdBtn.setToggleGroup(accountTypeGroup);

        // Group campus radio buttons
        newBrunswickBtn.setToggleGroup(campusGroup);
        newarkBtn.setToggleGroup(campusGroup);
        camdenBtn.setToggleGroup(campusGroup);

        branchBox.getItems().setAll("Edison", "Bridgewater", "Princeton", "Piscataway", "Warren");


        accountSelectBox.setPromptText("Choose an account");

        // CD Field Control Logic
        accountTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isCD = newToggle != null && ((RadioButton)newToggle).getText().equals("CD");
            cdTermBox.setDisable(!isCD);
            cdDatePicker.setDisable(!isCD);
        });

        // Start with them disabled
        cdTermBox.setDisable(true);
        cdDatePicker.setDisable(true);
    }

    @FXML
    private void handleOpenAccount() {
       String fname = firstNameField.getText().trim();
       String lname = lastNameField.getText().trim();
       LocalDate dobValue = dobPicker.getValue();

       if(fname.isEmpty() || lname.isEmpty() || dobValue == null ) {
           outputArea.appendText("Error: Please fill in all personal information. \n");
           return;
       }

       Date dob = new Date(dobValue.getMonthValue() + "/" + dobValue.getDayOfMonth() + "/" + dobValue.getYear());
       if(!dob.isValid() || !dob.isAdult()) {
           outputArea.appendText("Error: Invalid or undersage date of birth.\n");
           return;
       }
       RadioButton selectedAcct = (RadioButton) accountTypeGroup.getSelectedToggle();
       if(selectedAcct == null) {
           outputArea.appendText("Error: Please select and account type.\n");
           return;
       }

       String acctTypeStr = selectedAcct.getText();
       AccountType acctType = null;
       switch(acctTypeStr) {
           case "Checking":
               acctType = AccountType.CHECKING;
               break;
           case "Savings":
               acctType = AccountType.SAVINGS;
               break;
           case "Money Market":
               acctType = AccountType.MONEY_MARKET;
               break;
           case "College Checking":
               acctType = AccountType.COLLEGE_CHECKING;
               break;
           case "CD":
               acctType = AccountType.CD;
               break;
           default:
               outputArea.appendText("Error: Unknown account type selected.\n");
               return;
       }

       Profile holder = new Profile(fname, lname, dob);
        if (database.hasDuplicateType(holder, acctType)) {
            outputArea.appendText("Account already exists for this holder and account type.\n");
            return;
        }



        // New block for handling branch selection
        String selectedBranch = branchBox.getValue();
        if (selectedBranch == null) {
            outputArea.appendText("Error: Please select a branch.\n");
            return;
        }

        Branch branch;
        try {
            branch = Branch.valueOf(selectedBranch.toUpperCase());
        } catch (IllegalArgumentException e) {
            outputArea.appendText("Error: Invalid branch selected.\n");
            return;
        }

       AccountNumber accNum = new AccountNumber(branch, acctType);
       Account newAccount;

       //Read deposit input
        String depositText = depositField.getText().trim();
        double initDeposit;
        try {
            initDeposit = Double.parseDouble(depositText);
            if(initDeposit<0) {
                outputArea.appendText("Error: Initial deposit cannot be negative.\n");
                return;
            }
        } catch (NumberFormatException e) {
            outputArea.appendText("Error: Please enter a valid number for the deposit.\n");
            return;
        }

        if(acctType == AccountType.CHECKING) {
           newAccount = new Checking(accNum, holder, initDeposit);
       } else if (acctType == AccountType.SAVINGS) {
           newAccount = new Savings(accNum, holder, initDeposit);
       } else if (acctType == AccountType.MONEY_MARKET) {
           newAccount = new MoneyMarket(accNum, holder, initDeposit);
       } else if (acctType == AccountType.COLLEGE_CHECKING) {
           RadioButton selectedCampus = (RadioButton) campusGroup.getSelectedToggle();
           if ( selectedCampus == null) {
               outputArea.appendText("Error: Please select a campus for College Checking.\n");
               return;
           }
           Campus campus = Campus.NEW_BRUNSWICK; // Default
           switch (selectedCampus.getText()) {
               case "New Brunswick":
                   campus = Campus.NEW_BRUNSWICK;
                   break;
               case "Newark":
                   campus = Campus.NEWARK;
                   break;
               case "Camden":
                   campus = Campus.CAMDEN;
                   break;
           }
           newAccount = new CollegeChecking (accNum, holder, initDeposit, campus);
       } else if (acctType == AccountType.CD) {
            Integer term = cdTermBox.getValue();
            LocalDate openDateValue = cdDatePicker.getValue();

            if(term == null || openDateValue == null) {
                outputArea.appendText("Error: Please select a CD term and open date.\n");
                return;
            }

            Date cdOpenDate = new Date (openDateValue.getMonthValue() + "/" +openDateValue.getDayOfMonth() + "/" + openDateValue.getYear());
            newAccount = new CertificateDeposit(accNum, holder, initDeposit, term, cdOpenDate);
        } else {
            outputArea.appendText("Error: Unsupported account type.\n");
            return;
        }

        if (database.open(newAccount)) {
            outputArea.appendText("Account opened successfully: " + newAccount + "\n");
            refreshAccountComboBox();
        } else {
            outputArea.appendText("Account already exists: " + newAccount + "\n");
        }





        // Normally I was going to add it to the database but for now I just print it
        //outputArea.appendText(" Account opened successfully: " + newAccount + "\n");
    }


    /**
    @FXML
    private void handleCloseAccount() {
        String fname = firstNameField.getText().trim();
        String lname = lastNameField.getText().trim();
        LocalDate dobValue = dobPicker.getValue();

        if (fname.isEmpty() || lname.isEmpty() || dobValue == null) {
            outputArea.appendText("Error: Please fill in all personal information to close account.\n");
            return;
        }

        Date dob = new Date(dobValue.getMonthValue() + "/" + dobValue.getDayOfMonth() + "/" + dobValue.getYear());
        Profile holder = new Profile(fname, lname, dob);

        RadioButton selectedAcct = (RadioButton) accountTypeGroup.getSelectedToggle();
        if (selectedAcct == null) {
            outputArea.appendText("Error: Please select an account type to close.\n");
            return;
        }

        String acctTypeStr = selectedAcct.getText();
        AccountType acctType = null;
        switch (acctTypeStr) {
            case "Checking":
                acctType = AccountType.CHECKING;
                break;
            case "Savings":
                acctType = AccountType.SAVINGS;
                break;
            case "Money Market":
                acctType = AccountType.MONEY_MARKET;
                break;
            case "College Checking":
                acctType = AccountType.COLLEGE_CHECKING;
                break;
            case "CD":
                acctType = AccountType.CD;
                break;
            default:
                outputArea.appendText("Error: Unknown account type selected.\n");
                return;
        }

        RadioButton selectedBranch = (RadioButton) branchGroup.getSelectedToggle();
        if (selectedBranch == null) {
            outputArea.appendText("Error: Please select a branch to close the account.\n");
            return;
        }

        Branch branch;
        switch (selectedBranch.getText()) {
            case "Edison":
                branch = Branch.EDISON;
                break;
            case "Bridgewater":
                branch = Branch.BRIDGEWATER;
                break;
            case "Princeton":
                branch = Branch.PRINCETON;
                break;
            case "Piscataway":
                branch = Branch.PISCATAWAY;
                break;
            case "Warren":
                branch = Branch.WARREN;
                break;
            default:
                outputArea.appendText("Error: Invalid branch selected.\n");
                return;
        }

        AccountNumber accNum = new AccountNumber(branch, acctType);
        Account target = database.findByNumber(accNum.toString());

        if (target == null || !target.getHolder().equals(holder)) {
            outputArea.appendText("Account not found.\n");
            return;
        }

        database.closeAccount(target, new Date()); // using today's date
        outputArea.appendText("Account closed successfully: " + target + "\n");
    }
    */

    @FXML
    private void handleCloseAccount() {
        Account selectedAccount = accountSelectBox.getValue();

        if (selectedAccount == null) {
            outputArea.appendText("Please select an account to close.\n");
            return;
        }

        database.closeAccount(selectedAccount, new Date()); // using today's date
        outputArea.appendText("Account closed successfully: " + selectedAccount + "\n");

        refreshAccountComboBox(); // update the dropdown to reflect closed account
    }


    private void refreshAccountComboBox() {
        accountSelectBox.getItems().clear();

        for (int i = 0; i < database.size(); i++) {
            accountSelectBox.getItems().add(database.get(i));
        }
    }


    @FXML
    private void handleDeposit() {
        Account selectedAccount = accountSelectBox.getValue();
        if (selectedAccount == null) {
            outputArea.appendText("Please select an account to deposit into.\n");
            return;
        }

        String amtText = amountField.getText().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtText);
            if (amount <= 0) {
                outputArea.appendText("Deposit amount must be positive.\n");
                return;
            }
        } catch (NumberFormatException e) {
            outputArea.appendText("Invalid deposit amount.\n");
            return;
        }

        database.deposit(selectedAccount.getNumber(), amount);
        outputArea.appendText("Deposited $" + String.format("%.2f", amount) + " into " + selectedAccount + "\n");
    }

    @FXML
    private void handleWithdraw() {
        Account selectedAccount = accountSelectBox.getValue();
        if (selectedAccount == null) {
            outputArea.appendText("Please select an account to withdraw from.\n");
            return;
        }

        String amtText = amountField.getText().trim();
        double amount;
        try {
            amount = Double.parseDouble(amtText);
            if (amount <= 0) {
                outputArea.appendText("Withdrawal amount must be positive.\n");
                return;
            }
        } catch (NumberFormatException e) {
            outputArea.appendText("Invalid withdrawal amount.\n");
            return;
        }

        boolean success = database.withdraw(selectedAccount.getNumber(), amount);
        if (success) {
            outputArea.appendText("Withdrew $" + String.format("%.2f", amount) + " from " + selectedAccount + "\n");
        } else {
            outputArea.appendText("Withdrawal failed (insufficient funds or invalid input).\n");
        }
    }



    @FXML
    private void handleDisplayAccounts() {
        if (database.isEmpty()) {
            accountDisplayArea.setText("No open accounts to display.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < database.size(); i++) {
            sb.append(database.get(i).toString()).append("\n");
        }

        accountDisplayArea.setText(sb.toString());
    }


    @FXML
    private void handleFileOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            outputArea.appendText("📂 File selected: " + file.getAbsolutePath() + "\n");
        } else {
            outputArea.appendText("⚠️ No file selected.\n");
        }
    }

    @FXML
    private void handleClearFields() {
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        depositField.clear();

        accountTypeGroup.selectToggle(null);
        campusGroup.selectToggle(null);
        cdTermBox.setValue(null);
        cdDatePicker.setValue(null);
        branchBox.setValue(null);

        outputArea.clear();
    }



}
