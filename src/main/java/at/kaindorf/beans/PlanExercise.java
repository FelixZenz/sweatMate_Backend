package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanExercise {
    private int planId;
    private int exerciseId;
    private int num_reps;
    private int num_sets;
    private String details;
}
