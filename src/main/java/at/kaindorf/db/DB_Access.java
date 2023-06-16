package at.kaindorf.db;

import at.kaindorf.annotations.Column;
import at.kaindorf.annotations.Transient;
import at.kaindorf.beans.Plan;
import at.kaindorf.beans.User;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class DB_Access {
    private static DB_Access theInstance;
    private DB_Database database = DB_Database.getInstance();
    private Connection connection = database.getConnection();

    private DB_Access() throws SQLException, ClassNotFoundException {
    }

    public static DB_Access getInstance() throws SQLException, ClassNotFoundException {
        if (theInstance == null) {
            theInstance = new DB_Access();
        }
        return theInstance;
    }

    //allrounder function for inserting any type of object for an existing Class
    public Optional<Object> insertObject(Object entityObject) {
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
            try {
                fieldValues += isNumeric(field) ? field.get(entityObject).toString() +", "
                        : "'" +field.get(entityObject).toString()+"', ";
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        String sqlString = String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                fieldNames,
                fieldValues);
        sqlString = sqlString.replace(", )", ")");

        try{
            System.out.println(sqlString);
            database.getStatement().execute(sqlString);
            System.out.println("Insert successful");
            return Optional.of(entityObject);
        } catch (SQLException e){
            System.err.format("Insert INTO failed!");
            System.out.println(sqlString);
            System.err.println(e.toString());
            throw new NoSuchElementException("wrong Input - Insert Into failed");
        }
    }

    //check weather field is a Numeric type or not
    private boolean isNumeric(Field field){
        return field.getType().equals(int.class) || field.getType().equals(Integer.class)
                || field.getType().equals(long.class) || field.getType().equals(Long.class)
                || field.getType().equals(double.class) || field.getType().equals(Double.class)
                || field.getType().equals(boolean.class) || field.getType().equals(Boolean.class);
    }

    //Only for testing purpose
    /*
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("\n1) Create new Account\n2) Log into account\n3) Create New Plan\n#");
                int choice = scanner.nextInt();
                UserDB userDB = UserDB.getInstance();
                PlanDB planDB = PlanDB.getInstance();
                ExerciseDB exerciseDB = ExerciseDB.getInstance();
                DB_Access access = DB_Access.getInstance();
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
                        access.insertObject(user);
                        break;
                    case 2:
                        System.out.print("Username:");
                        String username = scanner.next();
                        System.out.print("\nPassword:");
                        String password = scanner.next();
                        System.out.print("\n" + userDB.login(username, password));
                        break;
                    case 3:
                        System.out.print("Name:");
                        String planname = scanner.next();
                        System.out.print("\nYour name:");
                        String uname = scanner.next();
                        User planUser = userDB.findUserByUsername(uname);
                        Plan plan = new Plan(planDB.getNewID(), planname, planUser.getUsername());
                        access.insertObject(plan);
                        planDB.fillPlanWithExercises(plan.getPlanid(), exerciseDB.loadAllExercises());
                        //System.out.println(plan.getPlanid());
                        //System.out.println("All exercises: " + exerciseDB.loadAllExercises());
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


    }
    */


}
