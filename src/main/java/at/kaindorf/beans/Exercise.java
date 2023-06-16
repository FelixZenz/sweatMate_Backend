package at.kaindorf.beans;

import at.kaindorf.annotations.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    @Column(name = "exerciseid")
    private int exerciseID;
    @Column(name = "exercise_name")
    private String exerciseName;
    @Column(name = "video")
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
