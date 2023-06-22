package at.kaindorf.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//Klasse für den Login mit usernamen und passwort
public class LoginData {
    private String username;
    private String pwd;
}
