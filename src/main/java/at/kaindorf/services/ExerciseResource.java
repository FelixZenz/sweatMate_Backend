package at.kaindorf.services;

import at.kaindorf.beans.Exercise;
import at.kaindorf.db.DB_Access;
import at.kaindorf.db.ExerciseDB;
import at.kaindorf.db.PlanDB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/exercise")
public class ExerciseResource {

    private DB_Access access = DB_Access.getInstance();
    private ExerciseDB exerciseDB = ExerciseDB.getInstance();

    public ExerciseResource() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
    }

    //to receive all exercises
    @GET
    public Response getAllExercises(){
        try {
            List<Exercise> exerciseList = exerciseDB.loadAllExercises();
            return Response.ok(exerciseList).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    //getExerciseByID
    @GET
    @Path("/{exerciseid}")
    public Response getExercisesForPlan(@PathParam("exerciseid") int exerciseid){
        Exercise exercise = new Exercise();
        exercise = exerciseDB.findExerciseById(exerciseid);
        return Response.ok(exercise).build();
    }
}
