import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Transaction implements Serializable {
    @Serial
    private static final long SerialVersionUID = 4L;
    int amount;
    String to;
    String time;

    //Constructor to create a Transaction whenever a transaction takes place.

    public Transaction(int amount, String to){
        this.amount = amount;
        this.to = to;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.time = now.format(formatter);
    }

    @Override
    public String toString() {
        return  "Amount : " + amount + "\n"+
                "Type : " + to + "\n" +
                "Time : " + time + "\n" ;
    }

    /* A ConcurrentHashMap returning method.
       Contains all transactions stored as list for a user against the username.
       Fetches data from transactions file.
    */
    public static ConcurrentHashMap<String, ArrayList<Transaction>> getTransaction(){
        File file = new File("src\\transactions.txt");
        if(file.exists() && file.length()>0) {
            ConcurrentHashMap<String, ArrayList<Transaction>> map;
            Main.Filelock.lock();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
                map = (ConcurrentHashMap<String, ArrayList<Transaction>>) ois.readObject();
                return map;
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Couldn't Fetch Data");
            }finally{
                Main.Filelock.unlock();
            }
        }
        return null;
    }

    /* A boolean returning method that returns if ConcurrentHashMap is saved back
       to file or not
    */
    public static boolean saveHashMap(ConcurrentHashMap<String, ArrayList<Transaction>> map){
        File file = new File("src\\transactions.txt");
        Main.Filelock.lock();
        try(ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file))){
            os.writeObject(map);
            return true;
        } catch (IOException e) {
            System.out.println("Some Error has Occurred while saving data...");
            return false;
        }finally {
            Main.Filelock.unlock();
        }
    }
}
