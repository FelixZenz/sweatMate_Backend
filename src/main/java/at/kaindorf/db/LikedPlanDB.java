package at.kaindorf.db;

import at.kaindorf.beans.LikedPlan;
import at.kaindorf.beans.Plan;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//Klasse für die Datenbankverbindungen speziell für die Tabelle LikedPlan
// --> auch andere LikedPlan Funktionen, neben den DB Funktionen, werden hier behandelt
public class LikedPlanDB {
    private DB_Database database = DB_Database.getInstance();
    private PlanDB planDB = PlanDB.getInstance();
    private List<LikedPlan> likedPlans = new ArrayList<>();
    private static LikedPlanDB instance;

    public LikedPlanDB() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        likedPlans = fillPlans();
    }

    public static LikedPlanDB getInstance() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        if (instance == null) {
            instance = new LikedPlanDB();
        }
        return instance;
    }

    //receive all plans from DB
    public List<LikedPlan> fillPlans() throws SQLException {
        List<LikedPlan> plans = new ArrayList<>();
        String sqlString = """
                SELECT * FROM likedplan;
                """;
        //username, planid, rating
        Statement statement = database.getStatement();
        ResultSet resultSet = statement.executeQuery(sqlString);
        while (resultSet.next()) {
            plans.add(new LikedPlan(resultSet.getString("username"),
                    resultSet.getInt("planid"), resultSet.getInt("rating")));
        }
        database.releaseStatement(statement);
        return plans;
    }

    //getAllLikedPlans
    public List<LikedPlan> getLikedPlans() {
        return likedPlans;
    }

    //1. Req
    //getLikedPlans for specific user (username)
    public List<Plan> getLikedPlansByUser(String username) {
        List<Plan> plans = new ArrayList<>();
        for (LikedPlan plan : likedPlans) {
            if (plan.getUsername().equals(username) && plan.getRating() == 1) {
                plans.add(planDB.getPlanByID(plan.getPlanid()));
            }
        }
        return plans;
    }

    //get liked plans by plan id
    public List<LikedPlan> getLikedPlansByPlanID(int planid) {
        List<LikedPlan> plans = new ArrayList<>();
        for (LikedPlan plan : likedPlans) {
            if (plan.getPlanid() == planid) {
                plans.add(plan);
            }
        }
        return plans;
    }


    //2. Req
    //everything about the rating of a plan
    public void addPlanRating(LikedPlan likedPlan) {
        String sqlString = "";
        boolean exists = false;
        for (LikedPlan lpLoop : likedPlans) {
            if (lpLoop.getUsername().equals(likedPlan.getUsername()) && lpLoop.getPlanid() == likedPlan.getPlanid()) {
                exists = true;
            }
        }
        if (!exists) {
            likedPlans.add(likedPlan);
            sqlString = "INSERT INTO likedplan VALUES ('" + likedPlan.getUsername() + "', " + likedPlan.getPlanid() + ", " + likedPlan.getRating() + ");";
            planDB.updatePlanRating(likedPlan.getPlanid(), likedPlan.getRating(), false);
        } else {
            for (LikedPlan lp : likedPlans) {
                if (lp.getUsername().equals(likedPlan.getUsername()) && lp.getPlanid() == likedPlan.getPlanid()) {
                    if (lp.getRating() != likedPlan.getRating()) {
                        lp.setRating(likedPlan.getRating());
                        sqlString = "UPDATE likedplan SET rating = " + likedPlan.getRating() +
                                " WHERE planid = " + likedPlan.getPlanid() + " AND username = '" + likedPlan.getUsername() + "';";
                        planDB.updatePlanRating(likedPlan.getPlanid(), likedPlan.getRating(), true);
                        break;
                    } else {
                        return;
                    }
                }
            }
        }
        try {
            database.getStatement().execute(sqlString);
            System.out.println("PlanRating successful");
        } catch (SQLException e) {
            System.err.format("PlanRating failed!");
            throw new RuntimeException(e);
        }

    }

    //delete a liked plan (called when a real plan gets deleted)
    public void deletePlan(int planid) {
        List<LikedPlan> plansToDelete = getLikedPlansByPlanID(planid);
        for (LikedPlan deletePlan : plansToDelete) {
            likedPlans.remove(deletePlan);
        }
        try {
            String sqlString = "DELETE FROM likedplan WHERE planid = " + planid + ";";
            database.getStatement().execute(sqlString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //um die Bewertung eines Planes für einen User zu bekommen
    public int getRatingFromUserForPlan(int planid, String username) {
        int rating = 0;
        for (LikedPlan likedPlan : likedPlans) {
            if (likedPlan.getPlanid() == planid && likedPlan.getUsername().equals(username)) {
                rating = likedPlan.getRating();
            }
        }
        return rating;
    }

}
