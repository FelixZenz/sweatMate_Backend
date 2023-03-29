package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;
    private List<Plan> likedPlans;
    private List<Plan> createdPlans;
}
