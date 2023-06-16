package at.kaindorf.db;

import at.kaindorf.beans.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class PlanDB {
    private DB_Access db_access = DB_Access.getInstance();
    private DB_Database database = DB_Database.getInstance();
    private List<Plan> planList = new ArrayList<>();
    private static PlanDB instance;

    public PlanDB() throws SQLException, ClassNotFoundException {
        planList = getAllPlans();
    }

    public static PlanDB getInstance() throws SQLException, ClassNotFoundException {
        if(instance == null){
            instance = new PlanDB();
        }
        return instance;
    }

    //receive all plans from DB
    public List<Plan> getAllPlans() throws SQLException {
        List<Plan> plans = new ArrayList<>();
        String sqlString = """
                            SELECT * FROM plan;
                            """;
        //planid, planname, likes, dislikes, creator
        Statement statement = database.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        while (resultSet.next()){
            plans.add(new Plan(resultSet.getInt("planid"),
                    resultSet.getString("planname"),resultSet.getInt("likes"),
                    resultSet.getInt("dislikes"), resultSet.getString("creator")));
        }
        return plans;
    }

    //Get ID for new Plan => getHighestID + 1 for the new TP
    public int getNewID() {
        String sqlString = """
                            SELECT MAX(planid) FROM plan;
                            """;
        Statement statement = null;
        try {
            statement = database.getStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int id = 0;
        try {
            id = resultSet.getInt("max");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id+1;
    }

    //check if the inserted exercise is a real exercise
    public boolean isRealExerciseID(int id){
        try {
            ExerciseDB db = ExerciseDB.getInstance();
            List<Exercise> exerciseList = db.loadAllExercises();
            Optional<Exercise> e = exerciseList.stream().filter(exercise -> exercise.getExerciseID() == id).findFirst();
            if (e.isPresent()){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    //returns single Plan by its ID
    public Plan getPlanByID(int id){
        return planList.stream().filter(p -> p.getPlanid() == id)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    //Update the rating of a plan (1 = like, 0 = dislike)
    public int updatePlanRating(int planid, int rating){
        Plan plan = getPlanByID(planid);
        int rates = 0;
        String type = "";
        if(rating == 1){
            rates = plan.getNumLikes() + 1;
            plan.setNumLikes(rates);
            type = "likes";
        }
        else {
            rates = plan.getNumDislikes() + 1;
            plan.setNumDislikes(rates);
            type = "dislikes";
        }

        String sqlString = "UPDATE plan SET " + type + " = "+ rates +" WHERE planid = " + planid+";";
        try {
            database.getStatement().execute(sqlString);
            System.out.println("Insert successful");
        } catch (SQLException e) {
            System.err.format("Insert INTO failed!");
            throw new RuntimeException(e);
        }
        return rates;
    }

    public List<PlanDetail> getDetailsToPlan(int id){
        List<PlanDetail> details = new ArrayList<>();
        try {
            String sqlString = "SELECT p.planid, p.planname, p.likes, p.dislikes, p.creator, pe.exerciseid, pe.num_sets, pe.num_reps, pe.details FROM plan p\n" +
                    "INNER JOIN planexercise pe on p.planid = pe.planid WHERE p.planid = " + id + ";";
            Statement statement = database.getStatement();
            ResultSet resultSet = statement.executeQuery(sqlString);
            while (resultSet.next()){
                //planid, planname, likes, dislikes, creator, exerciseid, num_sets, num_reps, details
                details.add(new PlanDetail(resultSet.getInt("planid"), resultSet.getString("planname"),
                        resultSet.getInt("likes"), resultSet.getInt("dislikes"), resultSet.getString("creator"),
                        resultSet.getInt("exerciseid"), resultSet.getInt("num_sets"), resultSet.getInt("num_reps"),
                        resultSet.getString("details")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return details;
    }
}
