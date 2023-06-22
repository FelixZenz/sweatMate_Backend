package at.kaindorf.services;

import at.kaindorf.beans.*;
import at.kaindorf.db.*;
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

//All Requests for Plan, LikedPlan & PlanExercise
@Path("/plan")
public class PlanResource {
    @Context
    private ContainerRequestContext crc;

    private DB_Access access = DB_Access.getInstance();
    private PlanDB planDB = PlanDB.getInstance();
    private PlanExerciseDB planExerciseDB = PlanExerciseDB.getInstance();
    private LikedPlanDB likedPlanDB = LikedPlanDB.getInstance();

    public PlanResource() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
    }

    //getAll Plans
    @GET
    @Path("/all")
    public Response getAllPlans() {
        List<Plan> planList = new ArrayList<>();
        planList = planDB.getAllPlans();
        return Response.ok(planList).build();
    }

    //Get sorted Plans
    @GET
    @Path("/all/{criteria}")
    public Response getSortedPlans(@PathParam("criteria") String criteria) {
        try {
            List<Plan> planList = new ArrayList<>();
            planList = planDB.getSortedPlans(criteria);
            return Response.ok(planList).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e).build();
        }

    }

    //getExercises for Plan
    @GET
    @Path("/{planId}")
    public Response getExercisesForPlan(@PathParam("planId") int planId) {
        List<PlanExercise> planExercises = new ArrayList<>();
        planExercises = planDB.getPlanexercisesForPlan(planId);
        return Response.ok(planExercises).build();
    }

    //get Plan By ID
    @GET
    @Path("/id/{planId}")
    public Response getPlanByID(@PathParam("planId") int planId) {
        Plan plan = planDB.getPlanByID(planId);
        return Response.ok(plan).build();
    }


    //insert new Plan (only planname with creator)
    @PUT
    @Path("/{planname}/{creator}")
    @JWTNeeded
    public Response insertPlan(@PathParam("planname") String planname, @PathParam("creator") String creator) {
        //String username = crc.getProperty("username").toString();
        int id = planDB.getNewID();
        Plan plan = new Plan(id, planname, creator);
        access.insertObject(plan);
        planDB.addPlan(plan);
        return Response.accepted(plan).build();
    }

    //insert exercise to a plan
    @PUT
    @Path("/pe")
    public Response addExercises(PlanExercise planExercise) {
        //System.out.println("########\n\n\n" + planExercise + "#####\n\n\n");
        try {
            Plan plan = planDB.getPlanByID(planExercise.getPlanId());
            System.out.println("#!#" + plan);
            planDB.insertExerciseToPlan(planExercise);
            if (plan == null) {
                throw new RuntimeException("Plan not found!");
            }
            return Response.ok(plan).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }


    //Delte a plan
    @DELETE
    @Path("/{planid}")
    public Response deletePlan(@PathParam("planid") int planId) {
        try {
            Plan plan = planDB.getPlanByID(planId);
            likedPlanDB.deletePlan(planId);
            planDB.deletePlan(plan);
            planDB.deletePlanFromDB(planId);
            return Response.ok(plan).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e).build();
        }
    }

    //get Plans from user --> table in the account page
    @JWTNeeded
    @GET
    @Path("/details/{username}")
    public Response getPlansFromUser(@PathParam("username") String username) {
        List<Plan> planList = planDB.getPlansFromUser(username);
        return Response.ok(planList).build();
    }


    //getLikedPlansFromUser --> like toggle button
    @JWTNeeded
    @GET
    @Path("/liked/{username}")
    public Response getLikedPlansForUser(@PathParam("username") String username) {
        List<Plan> planList = likedPlanDB.getLikedPlansByUser(username);
        return Response.ok(planList).build();
    }

    //getRatingForPlanFromUser
    @JWTNeeded
    @GET
    @Path("/liked/{username}/{planid}")
    public Response getRatingFromUserForPlan(@PathParam("username") String username, @PathParam("planid") int planid) {
        int rating = likedPlanDB.getRatingFromUserForPlan(planid, username);
        return Response.ok(rating).build();
    }


    //updatePlanRating
    @PUT
    @JWTNeeded
    @Path("/rate")
    public Response updatePlanRating(LikedPlan likedPlan) {
        try {
            likedPlanDB.addPlanRating(likedPlan);
            return Response.accepted().build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.CONFLICT).entity(e).build();
        }
    }

}
