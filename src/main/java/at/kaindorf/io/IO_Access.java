package at.kaindorf.io;

import at.kaindorf.beans.Exercise;
import at.kaindorf.db.DB_Access;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class IO_Access {
    public static List<Exercise> loadAllExercises() throws IOException {
        Path path = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "exercises.csv");
        return Files.readAllLines(path)
                .stream()
                .skip(1)
                .map(Exercise::new)
                .collect(Collectors.toList());
    }


    public static void main(String[] args) {
        try {
            System.out.println(loadAllExercises());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            DB_Access.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
