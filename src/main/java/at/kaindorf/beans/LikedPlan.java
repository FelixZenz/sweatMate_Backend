package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Klasse für LikedPlan (PlanID & username als PK)
public class LikedPlan {
    private String username;
    private int planid;
    private int rating;
}
