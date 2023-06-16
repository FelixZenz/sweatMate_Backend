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

public class UserDB {
    private DB_Access db_access = DB_Access.getInstance();
    private DB_Database database = DB_Database.getInstance();
    private static UserDB instance;
    private List<User> userList = new ArrayList<>();
    private Map<String, String> passwords = new HashMap<>();


    public static UserDB getInstance() throws SQLException, ClassNotFoundException {
        if(instance==null){
            instance = new UserDB();
        }
        return instance;
    }

    private UserDB() throws SQLException, ClassNotFoundException {
        userList = getAllUser();
        passwords = fillPasswords();
    }

    //function to get all Users
    public List<User> getAllUser() throws SQLException {
        List<User> users = new ArrayList<>();
        String sqlString = """
                            SELECT * FROM "user";
                            """;
        //username, firstname, lastname, email, pwd
        Statement statement = database.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        while (resultSet.next()){
            users.add(new User(resultSet.getString("username"),
                    resultSet.getString("firstname"), resultSet.getString("lastname"),
                    resultSet.getString("email"), resultSet.getString("pwd")));
        }
        return users;
    }

    //function to fill the passwords
    public Map<String, String> fillPasswords(){
        Map<String, String> pwd = new HashMap<>();
        for (User user : userList){
            if (!pwd.containsKey(user.getUsername())){
                pwd.put(user.getUsername(), user.getPassword());
            }
        }
        return pwd;
    }

    //login -> return username if successfully logged in, else throw exception
    public User login(String username, String passwd){
        String passwdDB = passwords.get(username);
        if(passwdDB != null && passwdDB.equals(passwd)){
            System.out.println("---successfully logged in---");
            return findUserByUsername(username);
        }
        throw new NoSuchElementException("Username or Password wrong!");
    }

    //return single User by unique username
    public User findUserByUsername(String username){
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }
}
