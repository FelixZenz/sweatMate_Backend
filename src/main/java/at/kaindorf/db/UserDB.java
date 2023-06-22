package at.kaindorf.db;

import at.kaindorf.annotations.Column;
import at.kaindorf.annotations.Transient;
import at.kaindorf.beans.Plan;
import at.kaindorf.beans.User;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

//Klasse für die Datenbankverbindungen speziell für die Tabelle User
// --> auch andere User Funktionen, neben den DB Funktionen, werden hier behandelt
public class UserDB {
    private DB_Database database = DB_Database.getInstance();
    private static UserDB instance;
    private List<User> userList = new ArrayList<>();
    private Map<String, String> passwords = new HashMap<>();


    public static UserDB getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) {
            instance = new UserDB();
        }
        return instance;
    }

    private UserDB() throws SQLException, ClassNotFoundException {
        userList = fillUser();
        passwords = fillPasswords();
    }

    //function to get all Users
    public List<User> fillUser() throws SQLException {
        List<User> users = new ArrayList<>();
        String sqlString = """
                SELECT * FROM "user";
                """;
        //username, firstname, lastname, email, pwd
        Statement statement = database.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        while (resultSet.next()) {
            users.add(new User(resultSet.getString("username"),
                    resultSet.getString("firstname"), resultSet.getString("lastname"),
                    resultSet.getString("email"), resultSet.getString("pwd")));
        }
        database.releaseStatement(statement);
        return users;
    }

    //function to fill the passwords
    public Map<String, String> fillPasswords() {
        Map<String, String> pwd = new HashMap<>();
        for (User user : userList) {
            if (!pwd.containsKey(user.getUsername())) {
                pwd.put(user.getUsername(), user.getPassword());
            }
        }
        return pwd;
    }

    //login -> return username if successfully logged in, else throw exception
    public User login(String username, String passwd) {
        String passwdDB = passwords.get(username);
        if (passwdDB != null && passwdDB.equals(passwd)) {
            System.out.println("---successfully logged in---");
            return findUserByUsername(username);
        }
        throw new NoSuchElementException("Username or Password wrong!");
    }

    //return single User by unique username
    public User findUserByUsername(String username) {
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    //Insert new User
    public void insertUser(User user) {
        if (!userList.contains(user)) {
            userList.add(user);
            passwords = fillPasswords();
        }
    }

    public List<User> getAllUser() {
        return userList;
    }

    //getAllUsernames
    public List<String> getAllUsernames() {
        List<String> userNames = new ArrayList<>();
        for (User user : userList) {
            if (!userNames.contains(user.getUsername())) {
                userNames.add(user.getUsername());
            }
        }
        return userNames;
    }

    //get User By Username --> show creator in plandetails
    public User getUserByUsername(String username) {
        User user = new User();
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                user = u;
            }
        }
        return user;
    }

    //check wheather the password is correct --> update user details
    public boolean checkUserPWD(String username, String password) {
        for (User u : userList) {
            if (u.getUsername().equals(username)) {
                if (u.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Update User
    public void updateUser(User user) {
        String sqlString = "";
        for (User u : userList) {
            if (u.getUsername().equals(user.getUsername())) {
                u.setFirstname(user.getFirstname());
                u.setLastname(user.getLastname());
                u.setEmail(user.getEmail());
                u.setPassword(user.getPassword());
                sqlString = "UPDATE \"user\" SET firstname = '" + user.getFirstname() + "', lastname = '" + user.getLastname() + "', email = '" + user.getEmail() + "', pwd = '" + user.getPassword() + "' WHERE username = '" + user.getUsername() + "';";
            }
        }
        try {
            database.getStatement().execute(sqlString);
            System.out.println("Update successful");
        } catch (SQLException e) {
            System.err.format("Update failed!");
            throw new RuntimeException(e);
        }
    }
}
