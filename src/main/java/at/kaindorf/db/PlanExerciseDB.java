package at.kaindorf.db;

import at.kaindorf.beans.Exercise;
import at.kaindorf.beans.Plan;
import at.kaindorf.beans.PlanExercise;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlanExerciseDB {
    private DB_Access db_access = DB_Access.getInstance();
    private DB_Database database = DB_Database.getInstance();
    private ExerciseDB exerciseDB = ExerciseDB.getInstance();
    private PlanDB planDB = PlanDB.getInstance();
    private List<PlanExercise> planexerciseList = new ArrayList<>();
    private static PlanExerciseDB instance;

    public PlanExerciseDB() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        planexerciseList = fillPlanExercises();
        planDB.fillPlansWithTheirExercises(planexerciseList);
    }

    public static PlanExerciseDB getInstance() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        if(instance == null){
            instance = new PlanExerciseDB();
        }
        return instance;
    }

    //receive all plans from DB
    public List<PlanExercise> fillPlanExercises() throws SQLException {
        List<PlanExercise> planExercises = new ArrayList<>();
        String sqlString = """
                            SELECT * FROM planexercise;
                            """;
        //planid, planname, likes, dislikes, creator
        Statement statement = database.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        while (resultSet.next()){
            planExercises.add(new PlanExercise(resultSet.getInt("planId"),
                    resultSet.getInt("exerciseId"),resultSet.getInt("num_reps"),
                    resultSet.getInt("num_sets"), resultSet.getString("details")));
        }
        database.releaseStatement(statement);
        return planExercises;
    }

    public List<PlanExercise> getAllPlanExercises(){
        return planexerciseList;
    }

    public List<PlanExercise> getPlanexercisesForSinglePlan (int planid) {
        return planDB.getPlanexercisesForPlan(planid);
    }
        /*
        List<PlanExercise> planEx = new ArrayList<>();
        for (PlanExercise exercise:planexerciseList) {
            if(exercise.getPlanId()==planid){
                planEx.add(exercise);
            }
        }
        return planEx;
    }
    */

}
