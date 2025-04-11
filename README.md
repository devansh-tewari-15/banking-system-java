# Java Banking System 💳

A multithreaded, file-based Java Banking System with admin and user roles. This project helps reinforce core Java concepts like serialization, concurrency, file locking, collections, and thread-safe operations.

---

## 🚀 Features

### 👨‍💼 Admin
- Add, Remove, and View all users.
- Automatically remove users who haven’t deposited within 3 minutes (background thread).
- View transactions of the users.

### 👤 User
- Create a bank account with unique username and account number.
- Secure login with password.
- Deposit and withdraw money (thread-safe).
- Transfer money between accounts with dual-locking for thread safety.
- View last few transactions (deposit, withdrawal, transfer).
- Delete account (only if balance is zero).
- Change password and check balance.
- Proper synchronization using `ReentrantLock`.

---

## 📦 Technologies Used

- **Java SE**
- **Multithreading**
- **Serialization**
- **File Locking**
- **ConcurrentHashMap**
- **ReentrantLock**
- **Collections API**

---

## 📁 File Structure

- `Main.java` – Entry point, handles login, menu, flow.
- `User.java` – Serializable class for user info & user-side operations.
- `Admin.java` – Admin class with administrative functions.
- `Transaction.java` – Serializable class to manage and store transactions per user.
- `Helper.java` – Utility class for file operations (load/save users and transactions).
- `LockManager.java` – Manages per-user locks using `ReentrantLock`.
- `users.txt` – Serialized list of all users.
- `transactions.txt` – Serialized map of user-wise transaction history.

---

## 💾 Data Handling

- User data is serialized and saved in `users.txt`.
- Transaction history is managed through a `ConcurrentHashMap<String, ArrayList<Transaction>>`.
- Every deposit, withdrawal, or transfer is logged with a timestamp and stored for later retrieval.

---

## 🧵 Concurrency & Thread Safety

- All critical operations (`deposit`, `withdraw`, `transfer`) run in separate threads.
- Each user has a lock from `LockManager` to avoid race conditions.
- Transfers acquire locks in a consistent order (alphabetically) to prevent deadlocks.

---

## 🛡️ Validations

- Prevents transfers to self.
- Prevents deletion if account has money.
- Requires password confirmation for sensitive actions.
- Input sanitization with fallback messages.

---

## 🧪 How to Run

1. Clone the repo:

```bash
git clone https://github.com/devansh-tewari-15/banking-system-java.git
cd banking-system-java
```

2. Compile and Run :

```bash
javac *.java
java Main
```
---
## 🔑 Default Credentials
You can use the following credentials to log in as an Admin:

👤 Username: admin  
🔒 Password: admin@123  

---
## 🛠️ Author
Devansh Tewari  
📧 devanshtewari15@example.com  
📌 Java | Multithreading | Systems Design  

---

## 📃 License
This project is open-source and free to use for educational or personal purposes.
