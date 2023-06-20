package at.kaindorf.services;

import at.kaindorf.beans.*;
import at.kaindorf.db.DB_Access;
import at.kaindorf.db.ExerciseDB;
import at.kaindorf.db.PlanDB;
import at.kaindorf.db.PlanExerciseDB;
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
    private PlanExerciseDB planExerciseDB = PlanExerciseDB.getInstance();

    public PlanResource() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
    }

    //getAll Plans
    @GET
    @Path("/all")
    public Response getAllPlans(){
        List<Plan> planList = new ArrayList<>();
        planList = planDB.getAllPlans();
        return Response.ok(planList).build();
    }

    //getExercises for Plan
    @GET
    @Path("/{planId}")
    public Response getExercisesForPlan(@PathParam("planId") int planId){
        List<PlanExercise> planExercises = new ArrayList<>();
        planExercises = planExerciseDB.getPlanexercisesForSinglePlan(planId);
        return Response.ok(planExercises).build();
    }

    //getExercises for Plan
    @GET
    @Path("/id/{planId}")
    public Response getPanByID(@PathParam("planId") int planId){
        Plan plan = planDB.getPlanByID(planId);
        return Response.ok(plan).build();
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
    public Response ratePlan(@PathParam("planid") int planid, @PathParam("rating") int rating){
       int num =  planDB.updatePlanRating(planid, rating);
       return Response.ok(num).build();
    }

    //
    @JWTNeeded
    @DELETE
    @Path("/{planid}")
    public Response deletePlan(@PathParam("planid") int planId){
        try{
            Plan plan = planDB.getPlanByID(planId);
            planDB.deletePlan(plan);
            if(plan == null){
                throw new RuntimeException("[!] Plan not found!");
            }
            return Response.ok(plan).build();
        }
        catch (RuntimeException e){
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    @JWTNeeded
    @GET
    @Path("/details/{username}")
    public Response getPlansFromUser(@PathParam("username") String username){
        List<Plan> planList = planDB.getPlansFromUser(username);
        return Response.ok(planList).build();
    }

}
