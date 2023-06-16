package at.kaindorf.services;

import at.kaindorf.beans.*;
import at.kaindorf.db.DB_Access;
import at.kaindorf.db.ExerciseDB;
import at.kaindorf.db.PlanDB;
import at.kaindorf.jwt.JWTNeeded;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/plan")
public class PlanResource {
    @Context
    private ContainerRequestContext crc;

    private DB_Access access = DB_Access.getInstance();
    private PlanDB planDB = PlanDB.getInstance();
    private ExerciseDB exerciseDB = ExerciseDB.getInstance();

    public PlanResource() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
    }

    //getAll Plans
    @GET
    @Path("/all")
    public Response getAllPlans(){
        List<Plan> planList = new ArrayList<>();
        try {
            planList = planDB.getAllPlans();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Response.ok(planList).build();
    }

    //getExercises for Plan
    @GET
    @Path("/{planid}")
    public Response getExercisesForPlan(@PathParam("planid") int planid){
        List<PlanDetail> details = new ArrayList<>();
        details = planDB.getDetailsToPlan(planid);
        return Response.ok(details).build();
    }


    //insert new Plan (only planname with creator)
    @PUT
    @Path("/{planname}")
    @JWTNeeded
    public Response insertPlan(@PathParam("planname") String planname){
        String username = crc.getProperty("username").toString();
        int id = planDB.getNewID();
        Plan plan = new Plan(id, planname, username);
        Optional<Object> planOptional = access.insertObject(plan);
        return Response.accepted(planOptional.get()).build();
    }

    //insert exercise to a plan
    @PUT
    @JWTNeeded
    public Response addExercises(PlanExercise planExercise){
        System.out.println("########\n\n\n" + planExercise + "#####\n\n\n");
        try{
            Plan plan = planDB.getPlanByID(planExercise.getPlanId());
            System.out.println("#!#" + plan);
            plan.addPlanExercise(planExercise);
            access.insertObject(planExercise);
            if(plan == null){
                throw new RuntimeException("Plan not found!");
            }
            return Response.ok(plan).build();
        }
        catch (RuntimeException e){
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    //rate a plan
    @PUT
    @Path("/{planid}/{rating}/rate")
    @JWTNeeded
    public Response ratePlan(@PathParam("planid") int planid, @PathParam("rating") int rating){
       int num =  planDB.updatePlanRating(planid, rating);

       return Response.ok(num).build();
    }

}
