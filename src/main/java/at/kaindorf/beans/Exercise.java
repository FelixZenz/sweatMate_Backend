package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    private int exerciseID;
    private String exerciseName;
    private byte[] byteArrayForVideoExercise;

    public Exercise(String line) {
        String[] tokens = line.split(";");
        this.exerciseID = Integer.parseInt(tokens[0]);
        this.exerciseName = tokens[1];
        this.byteArrayForVideoExercise = tokens[2].getBytes();
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseID=" + exerciseID +
                ", exerciseName='" + exerciseName + '\'' +
                ", byteArrayForVideoExercise=" + Arrays.toString(byteArrayForVideoExercise) +
                '}'+"normal= " +new String(byteArrayForVideoExercise, StandardCharsets.UTF_8) +"\n";
    }
}
