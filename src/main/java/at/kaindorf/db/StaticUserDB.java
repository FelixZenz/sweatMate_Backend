package at.kaindorf.db;

import at.kaindorf.annotations.Column;
import at.kaindorf.annotations.Transient;
import at.kaindorf.beans.User;

import java.io.FilterOutputStream;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class StaticUserDB {
    private DB_Access db_access = DB_Access.getInstance();
    private DB_Database database = DB_Database.getInstance();
    private static StaticUserDB instance;
    private List<User> userList = new ArrayList<>();
    private Map<String, String> passwords = new HashMap<>();

    public static StaticUserDB getInstance() throws SQLException, ClassNotFoundException {
        if(instance==null){
            instance = new StaticUserDB();
        }
        return instance;
    }

    private StaticUserDB() throws SQLException, ClassNotFoundException {
        userList = getAllUser();
        passwords = fillPasswords();
    }

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
        //JPC
    }

    public Map<String, String> fillPasswords(){
        Map<String, String> pwd = new HashMap<>();
        for (User user : userList){
            if (!pwd.containsKey(user.getUsername())){
                pwd.put(user.getUsername(), user.getPassword());
            }
        }
        return pwd;
    }



    public User login(String username, String passwd){
        String passwdDB = passwords.get(username);
        if(passwdDB != null && passwdDB.equals(passwd)){
            System.out.println("---successfully logged in---");
            return findUserByUsername(username);
        }
        throw new NoSuchElementException("user not found");
    }

    public User findUserByUsername(String username){
        return userList.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }



    public void insertObject(Object entityObject) throws SQLException, IllegalAccessException {
        Class entityClass = entityObject.getClass();
        String tableName =entityClass.getSimpleName().toLowerCase();
        if(tableName.equals("user")){
            tableName = "\"user\"";
            System.out.println(entityObject.toString());
        }
        String fieldNames = "";
        String fieldValues ="";
        for (Field field : entityClass.getDeclaredFields()){
            if(field.isAnnotationPresent(Transient.class)){
                continue;
            }
            String fieldName = field.getName().toLowerCase();
            if(field.isAnnotationPresent(Column.class) &&
                    !field.getAnnotation(Column.class).name().isEmpty()){
                fieldName = field.getAnnotation(Column.class).name();
            }
            fieldNames += fieldName +", ";
            field.setAccessible(true);
            fieldValues += isNumeric(field) ? field.get(entityObject).toString() +", "
                    : "'" +field.get(entityObject).toString()+"', ";

        }
        String sqlString = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                fieldNames,
                fieldValues);
        sqlString = sqlString.replace(", )", ")");

        try{
            database.getStatement().execute(sqlString);

            System.out.println("Account created");
        } catch (SQLException e){
            System.err.format("Insert INTO failed!");
            System.out.println(sqlString);
            System.err.println(e.toString());
        }
    }

    private boolean isNumeric(Field field){
        return field.getType().equals(int.class) || field.getType().equals(Integer.class)
                || field.getType().equals(long.class) || field.getType().equals(Long.class)
                || field.getType().equals(double.class) || field.getType().equals(Double.class)
                || field.getType().equals(boolean.class) || field.getType().equals(Boolean.class);
    }

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n1) Create new Account\n2) Log into account\n#");
                int choice = scanner.nextInt();
                StaticUserDB userDB = StaticUserDB.getInstance();
                switch (choice) {
                    case 1:
                        System.out.print("Username:");
                        String usernameNew = scanner.next();
                        System.out.print("\nFirstname:");
                        String firstname = scanner.next();
                        System.out.print("\nLastname:");
                        String lastname = scanner.next();
                        System.out.print("\nEmail:");
                        String email = scanner.next();
                        System.out.print("\nPassword:");
                        String passwdNew = scanner.next();
                        User user = new User(usernameNew, firstname, lastname, email, passwdNew);
                        userDB.insertObject(user);
                        break;
                    case 2:
                        System.out.print("Username:");
                        String username = scanner.next();
                        System.out.print("\nPassword:");
                        String password = scanner.next();
                        System.out.print("\n" + userDB.login(username, password));
                        break;
                    default: System.exit(0);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }
}
