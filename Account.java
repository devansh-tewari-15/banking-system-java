/*
Account methods are implemented by User and Admin
*/

public interface Account {
    String getUsername();
    boolean login();
    void showMenu();
    void logout();

}
