package at.kaindorf.beans;

import at.kaindorf.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Klasse für Übungen eines Planes (PlanID als PK)
public class PlanExercise {
    @Id
    private int planId;
    private int exerciseId;
    private int num_sets;
    private int num_reps;
    private String details;


}
