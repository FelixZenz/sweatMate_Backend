package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private String name;
    private User creator;
    private List<Exercise> exercieceList;
    private int numLikes;
    private int numDislikes;
}
