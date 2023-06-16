package at.kaindorf.beans;

import at.kaindorf.annotations.Column;
import at.kaindorf.annotations.Id;
import at.kaindorf.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    @Id
    private int planid;
    private String planname;
    @Transient
    private List<PlanExercise> exercieceList = new ArrayList<>();
    @Transient
    private int numLikes;
    @Transient
    private int numDislikes;
    private String creator;

    public Plan(int newID, String planname, String username) {
        this.planid = newID;
        this.planname = planname;
        this.creator = username;
    }

    public Plan(int planid, String planname, int likes, int dislikes, String creator) {
        this.planid = planid;
        this.planname = planname;
        this.numLikes = likes;
        this.numDislikes = dislikes;
        this.creator = creator;
    }

    public void addPlanExercise(PlanExercise exercise){

        if(!exercieceList.contains(exercise)){
            exercieceList.add(exercise);
        }
    }
}
