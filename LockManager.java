import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {
    // Saves Lock per user for file operation, stored in map so no need to create new again and again.
    private static final ConcurrentHashMap<String, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    // Used to retrieve the lock from map or create and store if there isn't one already.
    public static ReentrantLock getLockForUser(String username){
        userLocks.putIfAbsent(username, new ReentrantLock());
        return userLocks.get(username);
    }
}
