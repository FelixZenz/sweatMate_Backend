package at.kaindorf.db;

import at.kaindorf.beans.Exercise;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class ExerciseDB {
    private DB_Access db_access = DB_Access.getInstance();
    private DB_Database database = DB_Database.getInstance();
    private static ExerciseDB instance;
    private List<Exercise> exerciseList = new ArrayList<>();

    public ExerciseDB() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        exerciseList = loadAllExercises();
        //fillDBwithExercises(exerciseList);
    }

    public static ExerciseDB getInstance() throws SQLException, ClassNotFoundException, IOException, URISyntaxException {
        if(instance == null){
            instance = new ExerciseDB();
        }
        return instance;
    }

    //get all exercises from file
    public List<Exercise> loadAllExercises() throws IOException, URISyntaxException {
        //Path filepath = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "exercises.csv");
        URL url = ExerciseDB.class.getClassLoader().getResource("exercises.csv");
        return Files.readAllLines(Path.of(url.toURI()))
                .stream()
                .skip(1)
                .map(Exercise::new)
                .collect(Collectors.toList());
    }

    //get single exercise by id
    public Exercise findExerciseById(int id){
        return exerciseList.stream().filter(e -> e.getExerciseID() == id)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }


    public void fillDBwithExercises(List<Exercise> exercises){
        for (Exercise e: exerciseList) {
            db_access.insertObject(e);
        }
    }


    //only testing purpose

    public static void main(String[] args) {
        try {
            ExerciseDB exerciseDB = ExerciseDB.getInstance();
            System.out.println(exerciseDB.loadAllExercises());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


}
