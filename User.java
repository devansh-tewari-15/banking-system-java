import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class User implements Account, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // Stores the users currently logged in so that auto-removal doesn't check them.
    public static Set<String> loggedIn = Collections.synchronizedSet(new HashSet<>());
    private long createdAt;
    private String username;
    private String password;
    private String name;
    private String accountNumber;
    private int bankBalance;
    private boolean hasDeposited;

    @Override
    public String toString() {
        return "USERNAME : " + username + "\nNAME : " + name + "\nACCOUNT NUMBER : "
                + accountNumber + "\nBANK BALANCE : Rs."+bankBalance;
    }
    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setPassword(String new_password) {
        this.password = new_password;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getBankBalance() {
        return viewBalance();
    }

    public void setBankBalance(int bankBalance) {
        this.bankBalance = bankBalance;
    }

    public boolean getHasDeposited() {
        return hasDeposited;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }

    public void setHasDeposited(boolean hasDeposited) {
        this.hasDeposited = hasDeposited;
    }

    //Constructor to make a user when admin calls add a user.
    public User(String username, int bankBalance, String accountNumber, String name, String password) {
        this.username = username;
        this.bankBalance = bankBalance;
        this.accountNumber = accountNumber;
        this.name = name;
        this.password = password;
        this.createdAt = System.currentTimeMillis();
    }

    // called after matching if user exists in system to perform some session initiation operations.
    @Override
    public boolean login() {
        System.out.println(Main.ANSI_GREEN+"\nLogin successful...");
        loggedIn.add(getUsername());
        return true;
    }

    // Shows the user a list of operations that they can perform.
    @Override
    public void showMenu() {
        while (true) {
            System.out.println();
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================"+Main.ANSI_RESET);
            System.out.println(Main.ANSI_BLUE+"      WELCOME @" + this.getUsername() + "      "+Main.ANSI_RESET);
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================"+Main.ANSI_RESET);
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"[----- USER  MENU -----]");
            System.out.println("----- VIEW DETAILS -----     (Press 1)");
            System.out.println("------  DEPOSIT  -------     (Press 2)");
            System.out.println("----   WITHDRAW   ------     (Press 3)");
            System.out.println("------  TRANSFER   -----     (Press 4)");
            System.out.println("----  VIEW BALANCE  ----     (Press 5)");
            System.out.println("----  TRANSACTIONS  ----     (Press 6)");
            System.out.println("--- CHANGE PASSWORD  ---     (Press 7)");
            System.out.println("----  CLOSE ACCOUNT  ---     (Press 8)");
            System.out.println("------   LOGOUT   ------     (Press 9)");
            System.out.println("========================"+Main.ANSI_RESET);

            System.out.println("Enter Your Choice :");
            int choice;
            if (Main.sc.hasNextInt()) {
                choice = Main.sc.nextInt();
                Main.sc.nextLine();
            } else {
                System.out.println();
                System.out.println(Main.ANSI_RED+"\nEnter a number input"+Main.ANSI_RESET);
                Main.sc.nextLine();
                continue;
            }
            switch (choice) {
                case 1 -> showMyDetails();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> {
                    System.out.println(Main.ANSI_GREEN+"\nYour current Balance is :"+Main.ANSI_BLUE+" Rs." + viewBalance()+Main.ANSI_RESET);
                }
                case 6 -> showMyTransactions();
                case 7 -> changePassword();
                case 8 -> deleteMyAccount();
                case 9 -> logout();
                default -> {
                    System.out.println(Main.ANSI_RED+"\nEnter a valid number (1 - 9)"+Main.ANSI_RESET);
                }
            }
        //    System.out.println("\nPress Enter To Continue");
        //    Main.sc.nextLine();
        }
    }

    // Shows some details
    private void showMyDetails() {
        System.out.println(Main.ANSI_GREEN+"======= MY DETAILS =======");
        System.out.println("Name        - " + getName());
        System.out.println("Username    - " + getUsername());
        System.out.println("Account No. - " + getAccountNumber());
        System.out.println("=========================="+Main.ANSI_RESET);
    }

    // Change password (Also checks if it isn't same)
    public void changePassword() {
        System.out.println("Enter new password");
        String new_password = Main.sc.nextLine();
        System.out.println("Enter current password to confirm");
        String currentPassword = Main.sc.nextLine();
        if(new_password.equals(currentPassword)){
            System.out.println(Main.ANSI_RED+"\nPasswords are same");
            return;
        }
        if (currentPassword.equals(getPassword())) {
            ArrayList<User> users = Helper.getUserList();
            User current = null;
            for (User user : users) {
                if (user.getUsername().equalsIgnoreCase(getUsername())) {
                    current = user;
                    break;
                }
            }
            users.remove(current);
            current.setPassword(new_password);
            users.add(current);
            if (Helper.saveUserList(users)) {
                System.out.println(Main.ANSI_GREEN+"\nPassword changed successfully, Log-In Again"+Main.ANSI_RESET);
                Main.start();
            } else {
                System.out.println(Main.ANSI_RED+"Error Occured while creating password"+Main.ANSI_RESET);
            }
        } else {
            System.out.println(Main.ANSI_RED+"Old password didn't match"+Main.ANSI_RESET);
            showMenu();
        }
    }

    // Takes the user back to start page
    @Override
    public void logout() {
        System.out.println(Main.ANSI_BLUE+"\nLogging out..."+Main.ANSI_RESET);
        loggedIn.remove(getUsername());
        Main.start();
    }

    // File-Safe deposit method, creates and saves a transaction (validation checks present).
    public void deposit() {
        int amount = 0;
        while (true) {
            System.out.println("Enter The Amount:");
            if (Main.sc.hasNextInt()) {
                amount = Main.sc.nextInt();
                Main.sc.nextLine(); // consume newline
                if (amount <= 0) {
                    System.out.println(Main.ANSI_RED+"\nEnter a valid positive amount.\n"+Main.ANSI_RESET);
                    continue;
                }
                break;
            } else {
                System.out.println(Main.ANSI_RED+"\nEnter a valid amount.\n"+Main.ANSI_RESET);
                Main.sc.nextLine(); // consume invalid input
            }
        }

        int finalAmount = amount;
        Thread depositThread = new Thread(() -> {
            try {
                ArrayList<User> list = Helper.getUserList();
                for (User user : list) {
                    if (user.getUsername().equals(getUsername())) {
                        ReentrantLock lock = LockManager.getLockForUser(getUsername());
                        try {
                            System.out.println(Main.ANSI_BLUE+"\nDepositing money....please wait"+Main.ANSI_RESET);
                            lock.lock();
                            user.setBankBalance(user.getBankBalance() + finalAmount);
                            user.hasDeposited = true;
                            Transaction t = new Transaction(finalAmount, "SELF-DEPOSIT");
                            ConcurrentHashMap<String, ArrayList<Transaction>> map= Transaction.getTransaction();
                            if(map!=null) {
                                if(map.containsKey(getUsername())) {
                                    map.getOrDefault(getUsername(), null).add(t);
                                }
                                else{
                                    ArrayList<Transaction> arl = new ArrayList<>();
                                    arl.add(t);
                                    map.put(getUsername(), arl);
                                }
                            }
                            else{
                                map = new ConcurrentHashMap<>();
                                ArrayList<Transaction> trans = new ArrayList<>();
                                trans.add(t);
                                map.put(getUsername(),trans);
                            }
                            Transaction.saveHashMap(map);
                            break;
                        } finally {
                            lock.unlock();
                        }
                    }
                }

                Thread.sleep(2000); // for realism

                if (Helper.saveUserList(list)) {
                    System.out.println(Main.ANSI_GREEN+"\nRs."+finalAmount + " Deposited."+Main.ANSI_RESET);
                } else {
                    System.out.println(Main.ANSI_RED+"\nError occurred. Could not deposit."+Main.ANSI_RESET);
                }

            } catch (InterruptedException e) {
                System.out.println(Main.ANSI_RED+"\nOperation interrupted. Money not deposited."+Main.ANSI_RESET);
            }
        });
        depositThread.start();
        try {
            depositThread.join();
        } catch (InterruptedException e) {
            System.out.println(Main.ANSI_RED+"\nSome Error Occurred While Waiting for Deposit..."+Main.ANSI_RESET);
        }
    }

    // File-Safe withdraw method, creates and saves a transaction (validation checks present).
    public void withdraw() {
        int amount = 0;
        while (true) {
            viewBalance();
            System.out.println("How much money do you want to withdraw?");
            if (Main.sc.hasNextInt()) {
                amount = Main.sc.nextInt();
                //    Main.sc.nextLine();
                if (amount <= this.getBankBalance()) {
                    if(amount<=0){
                        System.out.println(Main.ANSI_RED+"\nEnter a positive amount..."+Main.ANSI_RESET);
                        continue;
                    }
                    break;
                } else {
                    System.out.println(Main.ANSI_RED+"\nAmount More Than Balance"+Main.ANSI_RESET);
                    Main.sc.nextLine();
                }
            } else {
                System.out.println(Main.ANSI_RED+"\nEnter a valid amount"+Main.ANSI_RESET);
                Main.sc.nextLine();
            }
        }
        int finalAmount = amount;
        Thread withdrawThread = new Thread(() -> {
            try {
                ArrayList<User> users = Helper.getUserList();
                for (User user : users) {
                    if (user.getUsername().equals(getUsername())) {
                        ReentrantLock lock = LockManager.getLockForUser(getUsername());
                        try {
                            lock.lock();
                            System.out.println(Main.ANSI_BLUE+"\nWithdrawing " + finalAmount + "..."+Main.ANSI_RESET);
                            user.setBankBalance(user.getBankBalance() - finalAmount);
                            if(user.getBankBalance()==0){
                                user.hasDeposited = false;
                                user.createdAt = System.currentTimeMillis();
                            }
                            Transaction t = new Transaction(finalAmount, "SELF-WITHDRAW");
                            ConcurrentHashMap<String, ArrayList<Transaction>> map= Transaction.getTransaction();
                            if(map!=null) {
                                if(map.containsKey(getUsername())) {
                                    map.getOrDefault(getUsername(), null).add(t);
                                }
                                else{
                                    ArrayList<Transaction> arl = new ArrayList<>();
                                    arl.add(t);
                                    map.put(getUsername(), arl);
                                }
                            }
                            else{
                                map = new ConcurrentHashMap<>();
                                ArrayList<Transaction> trans = new ArrayList<>();
                                trans.add(t);
                                map.put(getUsername(),trans);
                            }
                            Transaction.saveHashMap(map);
                        } finally {
                            lock.unlock();
                        }
                        break;
                    }
                }
                Thread.sleep(4000);
                if (Helper.saveUserList(users)) {
                    System.out.println("\n"+Main.ANSI_GREEN+"Rs."+finalAmount + " withdrew successfully"+Main.ANSI_RESET);
                    Main.sc.nextLine();
                } else {
                    System.out.println(Main.ANSI_RED+"\nError Occurred while saving Balance"+Main.ANSI_RESET);
                }
            } catch (Exception e) {
                System.out.println(Main.ANSI_RED+"\nSome Error Occurred while Updating Balance"+Main.ANSI_RESET);
            }
        });
        withdrawThread.start();
        try {
            withdrawThread.join();
        } catch (InterruptedException e) {
            System.out.println(Main.ANSI_RED+"\nSome Error Occurred while waiting"+Main.ANSI_RESET);
        }
    }

    // File-Safe method that gets the balance, can work instantly as transactions take place.
    public int viewBalance() {
        ArrayList<User> users = Helper.getUserList();
        User current = null;
        for (User user : users) {
            if (user.getUsername().equals(this.getUsername())) {
                current = user;
                break;
            }
        }
        // System.out.println("Your Current Balance is : Rs. " + current.getBankBalance());
        return current.bankBalance;
    }

    /*
        File-Safe that uses locks for both sender and receiver, sends message when transaction
        is completed, updates the data on both the accounts and manage transaction too.
    */
    public void transfer() {
        User that;
        User current;
        System.out.println("\nEnter Account Number for transfer : ");
        String thatAccount = Main.sc.nextLine();
        ArrayList<User> users = Helper.getUserList();
        that = users.stream().filter(user -> user.getAccountNumber().equals(thatAccount)).findFirst().orElse(null);
        if (that == null) {
            System.out.println(Main.ANSI_RED + "\nWrong Account Number..." + Main.ANSI_RESET);
            return;
        }

        current = users.stream().filter(user -> user.getUsername().equals(getUsername())).findFirst().orElse(null);
        if (current.hashCode() == that.hashCode()) {
            System.out.println(Main.ANSI_RED + "\nTransfer is for sending to other accounts" + Main.ANSI_RESET);
            return;
        }

        final int finalAmount;
        while (true) {
            System.out.println("Enter the amount you want to transfer : ");
            int amount;
            if (Main.sc.hasNextInt()) {
                amount = Main.sc.nextInt();
                if (amount <= current.viewBalance() && amount > 0) {
                    finalAmount = amount;
                    break;
                } else {
                    System.out.println(Main.ANSI_RED + "\nAmount More than Balance" + Main.ANSI_RESET);
                    Main.sc.nextLine();
                }
            } else {
                System.out.println(Main.ANSI_RED + "\nEnter Valid Number" + Main.ANSI_RESET);
                Main.sc.nextLine();
            }
        }

        Main.sc.nextLine();
        System.out.println("Enter your password to confirm : ");
        String confirmPassword = Main.sc.nextLine();
        if (!getPassword().equals(confirmPassword)) {
            System.out.println(Main.ANSI_RED + "\nWrong Password, Cancelling Transaction" + Main.ANSI_RESET);
            showMenu();
            return;
        }

        String user1 = getUsername();
        String user2 = that.getUsername();
        String first = user1.compareTo(user2) < 0 ? user1 : user2;
        String second = user1.equals(first) ? user2 : user1;

        ReentrantLock lockFirst = LockManager.getLockForUser(first);
        ReentrantLock lockSecond = LockManager.getLockForUser(second);

        Thread transferThread = new Thread(() -> {
            lockFirst.lock();
            try {
                lockSecond.lock();
                try {
                    System.out.println(Main.ANSI_GREEN + "\nTransferring the amount..." + Main.ANSI_RESET);
                    current.setBankBalance(current.getBankBalance() - finalAmount);
                    if (current.getBankBalance() == 0) {
                        current.hasDeposited = false;
                        current.createdAt = System.currentTimeMillis();
                    }
                    if(that.hasDeposited == false){
                        that.hasDeposited = true;
                    }

                    Transaction t1 = new Transaction(finalAmount, "sent to @" + that.getUsername() + " : " + that.getAccountNumber());
                    Transaction t2 = new Transaction(finalAmount, "received from @" + current.getUsername() + " : " + current.getAccountNumber());

                    ConcurrentHashMap<String, ArrayList<Transaction>> map = Transaction.getTransaction();
                    if (map == null) {
                        map = new ConcurrentHashMap<>();
                    }

                    map.computeIfAbsent(current.getUsername(), k -> new ArrayList<>()).add(t1);
                    map.computeIfAbsent(that.getUsername(), k -> new ArrayList<>()).add(t2);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        System.out.println(Main.ANSI_RED + "\nSome Error Has Occurred" + Main.ANSI_RESET);
                    }

                    System.out.println(Main.ANSI_GREEN + "Adding Amount to the other account..." + Main.ANSI_RESET);
                    that.setBankBalance(that.getBankBalance() + finalAmount);

                    boolean saveTrans = Transaction.saveHashMap(map);
                    boolean saveUsers = Helper.saveUserList(users);

                    if (saveTrans && saveUsers) {
                        System.out.println(Main.ANSI_GREEN + "\nTransfer Successful" + Main.ANSI_RESET);
                    } else {
                        System.out.println(Main.ANSI_RED + "\nError processing the transfer" + Main.ANSI_RESET);
                    }

                } finally {
                    lockSecond.unlock();
                }
            } finally {
                lockFirst.unlock();
            }
        });

        transferThread.start();
        try {
            transferThread.join();
        } catch (Exception e) {
            System.out.println(Main.ANSI_RED + "\nSome error occurred while waiting" + Main.ANSI_RESET);
        }
    }


    // Checks the balance and if 0, removes the user and its transaction data.
    private void deleteMyAccount() {
        User current = null;
        ArrayList<User> users = Helper.getUserList();
        System.out.println("\nEnter Password to confirm : ");
        String confirmPassword = Main.sc.nextLine();
        if (confirmPassword.equals(getPassword())) {
            for (User user : users) {
                if (user.getUsername().equals(getUsername())) {
                    current = user;
                    break;
                }
            }
            if (current.getBankBalance() == 0) {
                ConcurrentHashMap<String, ArrayList<Transaction>> map= Transaction.getTransaction();
                if(map!=null) {
                    if(map.containsKey(current.getUsername())) {
                        map.remove(current.getUsername());
                    }
                }
                Transaction.saveHashMap(map);
                users.remove(current);
                if (Helper.saveUserList(users)) {
                    System.out.println(Main.ANSI_BLUE+"\nAccount Deleted"+Main.ANSI_RESET);
                    Main.start();
                } else {
                    System.out.println(Main.ANSI_RED+"\nSome error while deleting account"+Main.ANSI_RESET);
                }
            } else {
                System.out.println(Main.ANSI_RED+"\nWithdraw the balance to proceed with deletion"+Main.ANSI_RESET);

            }
        } else {
            System.out.println(Main.ANSI_RED+"\nPasswords Didn't Match, Try Again"+Main.ANSI_RESET);
        }
    }

    // Shows all the transactions User has done, their type and time.
    public void showMyTransactions(){
        ConcurrentHashMap<String, ArrayList<Transaction>> map = Transaction.getTransaction();
        if(map==null){
            System.out.println(Main.ANSI_RED+"\nNo Transactions Yet"+Main.ANSI_RESET);
        }
        else{
            if(map.containsKey(getUsername())){
                ArrayList<Transaction> list= map.get(getUsername());
                if(list.isEmpty()){
                    System.out.println(Main.ANSI_RED+"\nNo Transactions to show"+Main.ANSI_RESET);
                }
                else {
                    System.out.print(Main.ANSI_GREEN+"\n----------------TRANSACTIONS---------------");
                    for (Transaction t : list) {
                        System.out.println("\n===========================================");
                        System.out.println(t.toString());
                    }
                    System.out.println("============================================"+Main.ANSI_RESET);
                    System.out.println("Press Enter To Go To Menu");
                    Main.sc.nextLine();
                    return;
                }
            }
            else{
                System.out.println(Main.ANSI_RED+"\nNo Transactions Yet"+Main.ANSI_RESET);
            }
        }
    }
}
