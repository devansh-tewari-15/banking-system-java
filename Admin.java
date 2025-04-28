import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Admin implements Account {

    // Hardcoded username and password
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin@123";

    private String username;
    private String password;

    private ArrayList<User> localList;

    // Static method to initiate the Admin session
    public static void initiateAdminSession() {
        Admin admin = new Admin();
        admin.loginPrompt();
    }

    // Prompting user for login
    private void loginPrompt() {
        System.out.print("Enter Username: ");
        System.out.println();
        username = Main.sc.nextLine();
        System.out.print("Enter Password: ");
        System.out.println();
        password = Main.sc.nextLine();

        if (login()) {
            System.out.println(Main.ANSI_GREEN + "\nLogin Successful.\n" +Main.ANSI_RESET);
            showMenu();
        } else {
            System.out.println(Main.ANSI_RED+"\nWrong username or password. Redirecting to main menu...\n"+Main.ANSI_RESET);
            Main.start();
        }
    }

    // For UI Purpose
    @Override
    public String getUsername() {
        return username;
    }

    // Validates the Account as Admin's and shows the Admin Menu.
    @Override
    public boolean login() {
        return username.equalsIgnoreCase(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    //Provides the menu for operations to admin.
    @Override
    public void showMenu() {
        while (true) {
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================"+Main.ANSI_RESET);
            System.out.println(Main.ANSI_BLUE+"----  WELCOME " + getUsername() + " ----"+Main.ANSI_RESET);
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================");
            Main.printQueuedMessages();
            if(Main.hasPrinted){
                Main.hasPrinted = false;
                System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================"+Main.ANSI_RESET);
                System.out.println(Main.ANSI_BLUE+"----  WELCOME " + getUsername() + " ----"+Main.ANSI_RESET);
                System.out.println(Main.ANSI_BRIGHT_YELLOW+"========================");
            }
            System.out.println("[----- ADMIN MENU -----]");
            System.out.println("------ VIEW USERS ------     (Press 1)");
            System.out.println("------  ADD USER  ------     (Press 2)");
            System.out.println("------ REMOVE USER -----     (Press 3)");
            System.out.println("--  VIEW TRANSACTIONS --     (Press 4)");
            System.out.println("------   LOGOUT   ------     (Press 5)");
            System.out.println("========================");
            System.out.println(Main.ANSI_RESET);
            System.out.print("Enter Your Choice : ");
            int choice;
            if (Main.sc.hasNextInt()) {
                choice = Main.sc.nextInt();
                Main.sc.nextLine();
            } else {
                System.out.println();
                System.out.println(Main.ANSI_RED+"Enter a number input\n"+Main.ANSI_RESET);
                Main.sc.nextLine();
                continue;
            }
            switch (choice) {
                case 1 -> showUsers();
                case 2 -> addUser();
                case 3 -> removeUsers();
                case 4 -> showTransactions();
                case 5 -> logout();
                default -> {
                    System.out.println(Main.ANSI_RED+"Enter Number between 1 to 5"+Main.ANSI_RESET);
                }
            }
        }

    }

    // Logouts and take to start page.
    @Override
    public void logout() {
        System.out.println(Main.ANSI_BLUE+"\nLogging out...\n"+Main.ANSI_RESET);
        Main.start();
    }

    // Adds a new User
    public void addUser() {
        localList = Helper.getUserList();
        if (localList == null) {
            localList = new ArrayList<>();
        }
        // STEP 2: Take user input
        System.out.print("Enter Name : ");
        String gName = Main.sc.nextLine();
        System.out.print("Enter username : ");
        String uName = Main.sc.nextLine();
        if (!isUniqueUserName(localList, uName)) {
            System.out.println("\n"+Main.ANSI_RED+ "Username already exists" + Main.ANSI_RESET+"\n");
            showMenu();
        }
        System.out.print("Enter a password : ");
        String uPassword = Main.sc.nextLine();
        String accNumber = generateAccountNumber();
        if (!isUniqueAccountNumber(localList, accNumber)) {
            System.out.println(Main.ANSI_RED+"Account Number already exists"+ Main.ANSI_RESET);
            showMenu();
        }
        // STEP 3: Add and write user list back to file
        localList.add(new User(uName, 0, accNumber, gName, uPassword));
        if (Helper.saveUserList(localList)) {
            System.out.println();
            System.out.println(Main.ANSI_GREEN+"User Added..."+ Main.ANSI_RESET);
        } else {
            System.out.println(Main.ANSI_RED+"Failed to add Data"+ Main.ANSI_RESET);
        }
        System.out.println();
        showMenu();
    }

    // Shows the list of all Users in system.
    public void showUsers() {
        localList = Helper.getUserList();
        if (localList == null ||localList.isEmpty()) {
            System.out.println(Main.ANSI_RED + "No users added yet" +Main.ANSI_RESET);
        } else {
            System.out.println();
            System.out.println("=============================");
            for (User user : localList) {
                System.out.println(Main.ANSI_GREEN+user.toString()+Main.ANSI_RESET);
                System.out.println("=============================");
                System.out.println();
            }
            System.out.println("Press Enter To See Menu");
            Main.sc.nextLine();
        }
    }

    // Removes the User after checking if balance is 0.
    public void removeUsers() {
        localList = Helper.getUserList();
        if (localList == null) {
            System.out.println(Main.ANSI_RED+"\nNo user present in the system..."+Main.ANSI_RESET);
            showMenu();
            return;
        }

        System.out.print("\nEnter username of the user to be removed : ");
        String tbrUsername = Main.sc.nextLine();
        if (isUniqueUserName(localList, tbrUsername)) {
            System.out.println(Main.ANSI_RED+"\nUsername not present in the system...\n"+ Main.ANSI_RESET);
            showMenu();
        } else {
            User temp = null;
            for (User user : localList) {
                if (user.getUsername().equalsIgnoreCase(tbrUsername)) {
                    temp = user;
                    break;
                }
            }
            if(temp.getBankBalance()!=0){
                System.out.println(Main.ANSI_RED+"\nAsk The User to Withdraw Money First\n"+Main.ANSI_RESET);
                return;
            }
            localList.remove(temp);
            ConcurrentHashMap<String, ArrayList<Transaction>> map = Transaction.getTransaction();
            if(map.remove(temp.getUsername())!=null) {
                Transaction.saveHashMap(map);
            }
            Helper.saveUserList(localList);
            System.out.println(Main.ANSI_GREEN+"\n"+temp.getUsername()+" removed...\n"+Main.ANSI_RESET);
        }
    }

    // Shows the transaction for user that admin want to see.
    public void showTransactions(){
        ArrayList<User> users = Helper.getUserList();
        if(users==null||users.size()==0){
            System.out.println(Main.ANSI_RED+"No users in the system yet..."+Main.ANSI_RESET);
            showMenu();
        }
        else{
            System.out.println(Main.ANSI_BRIGHT_YELLOW+"\nUsers In The System : "+Main.ANSI_RESET);
            for(User user : users){
                System.out.println(Main.ANSI_GREEN+user.getUsername()+Main.ANSI_RESET);
            }
            System.out.println();
            System.out.println("For Whom Do You Wanna See The Transactions ? ");
            String uName = Main.sc.nextLine();
            if(isUniqueUserName(users, uName)){
                System.out.println(Main.ANSI_RED+"\nUsername Not In The System"+Main.ANSI_RESET);
                System.out.println("Press Enter To Continue");
                Main.sc.nextLine();
                System.out.println();
                showMenu();
                return;
            }
            User required = null;
            for(User user: users){
                if(user.getUsername().equals(uName)){
                    required = user;
                    break;
                }
            }
            required.showMyTransactions();
            showMenu();
        }
    }

    // Helper method to generate account number
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 1; i <= 11; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Helper method to check if userName is unique
    private boolean isUniqueUserName(ArrayList<User> list, String uName) {
        for (User user : list) {
            if (user.getUsername().equalsIgnoreCase(uName)) {
                return false;
            }
        }
        return true;
    }

    // Helper method to check if account number is unique
    private boolean isUniqueAccountNumber(ArrayList<User> list, String accNum) {
        for (User user : list) {
            if (user.getAccountNumber().equals(accNum)) {
                return false;
            }
        }
        return true;
    }
}
