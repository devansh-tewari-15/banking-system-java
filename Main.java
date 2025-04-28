import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    // ANSI Codes for Colored Console texts.
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE ="\u001B[34m";
    public static final String ANSI_BRIGHT_YELLOW =  "\u001B[38;5;229m";

    // Checks if messageQueue has printed or not.
    public static boolean hasPrinted = false;

    // Locking the files before writing, reading and updating operations to save data loss.
    public static ReentrantLock Filelock = new ReentrantLock();

    // Reading inputs from user.
    public static final Scanner sc = new Scanner(System.in);

    // Used to store messages from auto-remove thread and show when admin is not doing anything.
    public static ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue();

    // Delegate Main method that starts auto-removal thread and calls start page.
    public static void main(String[] args) {
        Helper.startAutoRemoveThread();
        start();
    }

    // Used to check the message queue and print data (Called before admin starts an operation.
    public static void printQueuedMessages(){
        while((!Main.messageQueue.isEmpty())){
            System.out.println(ANSI_RED + Main.messageQueue.poll() + ANSI_RESET);
            hasPrinted = true;
        }
    }

    // Start Menu which starts admin or user session and provides quit option.
    public static void start() {
        System.out.println(ANSI_BRIGHT_YELLOW);
        System.out.println("===================");
        System.out.println("WELCOME TO THE BANK");
        System.out.println("===================");
        System.out.println("------ MENU -------");
        System.out.println("--- ADMIN LOGIN ---    (PRESS 1)");
        System.out.println("---  USER LOGIN ---    (PRESS 2)");
        System.out.println("---   QUIT APP  ---    (PRESS 3)");
        System.out.println("===================");
        System.out.println(ANSI_RESET);
        System.out.print("Enter Your Choice: ");
        while (true) {
            int choice;
            while (true) {
                System.out.println();
                if (sc.hasNextInt()) {
                    choice = sc.nextInt();
                    sc.nextLine(); // clear buffer
                    break;
                } else {
                    System.out.println();
                    System.out.println(ANSI_RED+"Invalid input. Please enter a number (1-3)."+ANSI_RESET);
                    sc.nextLine(); // flush invalid input
                }
            }
            switch (choice) {
                case 1 -> {
                    Admin.initiateAdminSession();
                }
                case 2 -> {
                    Helper.startLogin();
                }
                case 3 -> {
                    System.out.println();
                    System.out.println(ANSI_BLUE+"Thank you for using the Bank App. Goodbye!"+ANSI_RESET);
                    System.exit(0);
                }
                default -> {
                    System.out.println();
                    System.out.println(ANSI_RED+"Invalid choice. Please select 1, 2, or 3."+ANSI_RESET);
                }
            }
        }
    }
}
