package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanDetail {
    int planid;
    String planname;
    int likes;
    int dislikes;
    String creator;
    int exerciseid;
    int num_sets;
    int num_reps;
    String details;
}
