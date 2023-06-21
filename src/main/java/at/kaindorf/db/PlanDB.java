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
    private ExerciseDB exerciseDB = ExerciseDB.getInstance();
    private List<Plan> planList = new ArrayList<>();
    private static PlanDB instance;

    public PlanDB() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        planList = fillPlans();
    }

    public static PlanDB getInstance() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        if(instance == null){
            instance = new PlanDB();
        }
        return instance;
    }

    //receive all plans from DB
    public List<Plan> fillPlans() throws SQLException {
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
        database.releaseStatement(statement);
        return plans;
    }

    public List<Plan> getAllPlans(){
        return planList;
    }

    public List<Plan> getSortedPlans(String criteria) {
        List<Plan> plans = planList;
        if (criteria.equals("best")) {
            Collections.sort(plans, Comparator.comparingInt(Plan::getNumLikes).reversed().thenComparing(Plan::getNumDislikes));
        } else {
            Collections.sort(plans, Comparator.comparingInt(Plan::getNumDislikes).reversed().thenComparing(Plan::getNumLikes));
        }
        return plans;
    }

    //Get ID for new Plan => getHighestID + 1 for the new TP
    public int getNewID() {
        int id = -1; // Initialisieren Sie die höchste Plan-ID mit einem negativen Wert
        for (Plan plan : planList) {
            int currentPlanId = plan.getPlanid();
            if (currentPlanId > id) {
                id = currentPlanId;
            }
        }
        return id +1;

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


    //SELECT * FROM planexercise WHERE planid = 1;
    public List<PlanExercise> getExercisesForPlan(int planId){
        List<PlanExercise> planExercises = new ArrayList<>();
        Plan plan = getPlanByID(planId);
        planExercises = plan.getExercieceList();
        return planExercises;
    }

    public void deletePlan(Plan plan){
        if(planList.contains(plan)){
            planList.remove(plan);
        }
    }

    public void deletePlanFromDB(int id){
        try {
            String sqlStringForPE = """ 
                            DELETE FROM planexercise WHERE "planId" = """ + id + ";";
            String sqlString = "DELETE FROM plan WHERE planid = " + id + ";";
            database.getStatement().execute(sqlStringForPE);
            database.getStatement().execute(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Plan> getPlansFromUser(String username){
        List<Plan> plans = new ArrayList<>();
        for (Plan plan:planList) {
            if(plan.getCreator().equals(username)){
                plans.add(plan);
            }
        }
        return plans;
    }

    public void addPlan(Plan plan){
        if(!planList.contains(plan)){
            planList.add(plan);
        }
    }

    public void insertExerciseToPlan(PlanExercise planExercise){
        int id = planExercise.getPlanId();
        for (Plan plan : planList){
            if(plan.getPlanid()==id){
                plan.addPlanExercise(planExercise);
                planList.remove(plan);
                planList.add(plan);
            }
        }
        try {
            //INSERT INTO planexercise VALUES (1, 2, 3, 4, "details");
            String sqlString = "INSERT INTO planexercise VALUES (" + planExercise.getPlanId()
                    + ", " + planExercise.getExerciseId() + ", " + planExercise.getNum_sets()
                    + ", " + planExercise.getNum_reps() + ", '" + planExercise.getDetails() + "');";
            database.getStatement().execute(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PlanExercise> getPlanexercisesForPlan(int id){
        List<PlanExercise> planExerciseList = new ArrayList<>();
        for (Plan plan : planList){
            if (plan.getPlanid() == id){
                planExerciseList = plan.getExercieceList();
            }
        }
        return planExerciseList;
    }

    public void fillPlansWithTheirExercises(List<PlanExercise> planExercises){
        List<Plan> plans = new ArrayList<>();
        for (Plan plan : planList){
            for (PlanExercise planExercise:planExercises) {
                if(plan.getPlanid() == planExercise.getPlanId()){
                    if(!plans.contains(plan)){
                        plans.add(plan);
                    }
                    plan.addPlanExercise(planExercise);
                }
            }
        }
        planList = plans;
    }
}
