package at.kaindorf.beans;

import at.kaindorf.annotations.Column;
import at.kaindorf.annotations.Id;
import at.kaindorf.annotations.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    @Column(name = "pwd")
    private String password;
    @Transient
    private List<Plan> likedPlans;
    @Transient
    private List<Plan> createdPlans;

    public User(String username, String firstname, String lastname, String email, String password) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }
}
