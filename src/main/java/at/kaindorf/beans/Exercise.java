package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exercise {
    private String exerciseName;
    private int numberOfSets;
    private int numberOfReps;
    private String detailedInformation;
    private Byte[] byteArrayForVideoExercise;
}
