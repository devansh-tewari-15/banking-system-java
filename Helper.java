import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Helper {
    // Helper Method to get the list of users in the system from the stored file.
    public static ArrayList<User> getUserList() {
        File file = new File("src\\users.txt");
        // STEP 1: Read existing users if file exists and has data
        if (file.exists() && file.length() > 0) {
            ArrayList<User> userList;
            Main.Filelock.lock();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                userList = (ArrayList<User>) ois.readObject();
                return userList;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Failed to read user data. Starting with empty list.");
            }
            finally{
                Main.Filelock.unlock();
            }
        }
        return null;
    }

    // Helper Method that returns if List is saved back to file or not.
    public static boolean saveUserList(ArrayList<User> localList) {
        File file = new File("src\\users.txt");
        Main.Filelock.lock();
        try (ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream(file))) {
            obj.writeObject(localList);
            return true;
        } catch (IOException e) {
            return false;
        }
        finally{
        Main.Filelock.unlock();
        }
    }

    // Helper Method that starts the authentication for user
    public static void startLogin(){
        System.out.print("Enter username : ");
        String thisUsername = Main.sc.nextLine();
        System.out.print("Enter password : ");
        String thisPassword = Main.sc.nextLine();
        ArrayList<User> array = Helper.getUserList();
        if(array==null){
            System.out.println(Main.ANSI_RED+"\nNo users in the system yet"+Main.ANSI_RESET);
            Main.start();
            return;
        }
        for(User user : array){
            if(user.getUsername().equalsIgnoreCase(thisUsername) && user.getPassword().equals(thisPassword)){
                if(user.login()){
                    user.showMenu();
                    return;
                }
            }
        }
        System.out.println(Main.ANSI_RED+"\nWrong username or password. Redirecting to main menu...\n"+Main.ANSI_RESET);
        Main.start();
    }

    // Auto-remove users thread that runs in background (File-safe)
    public static void startAutoRemoveThread() {
        Thread autoRemoveThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60*1000);
                    ArrayList<User> userList = Helper.getUserList();
                    if(userList == null){
                        continue;
                    }
                    boolean changed = false;
                    long now = System.currentTimeMillis();
                    Iterator<User> iterator = userList.iterator();
                    while (iterator.hasNext()) {
                        User user = iterator.next();
                        if(User.loggedIn.contains(user.getUsername())){
                            continue;
                        }
                        if (!user.getHasDeposited() && now - user.getCreatedAt() > 4*60*1000) {
                            changed = true;
                            Main.messageQueue.add("Auto-removed user : "+user.getUsername());
                            iterator.remove();
                        }
                    }
                    if (changed) {
                        Helper.saveUserList(userList);
                    }
                } catch (InterruptedException e) {
                    Main.messageQueue.add("Auto-removal thread interrupted");
                }
            }
        });
        autoRemoveThread.setDaemon(true);
        autoRemoveThread.start();
    }
}

