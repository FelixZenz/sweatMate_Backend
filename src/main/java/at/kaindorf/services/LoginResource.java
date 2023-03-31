package at.kaindorf.services;

import at.kaindorf.beans.LoginData;
import at.kaindorf.beans.User;
import at.kaindorf.db.StaticUserDB;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Path("/login")
public class LoginResource {
    private StaticUserDB staticUserDB = StaticUserDB.getInstance();
    public static final String JWT_SECRET ="this-is-my-not-very-long-should-be-much-much-longer";

    public LoginResource() throws SQLException, ClassNotFoundException {
    }

    private String createJWT(User user) throws JOSEException {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                new Payload(user.getEmail()));
        jwsObject.sign(new MACSigner(JWT_SECRET.getBytes()));
        System.out.println(jwsObject.serialize());
        return jwsObject.serialize();
    }

    @POST
    public Response login(LoginData loginData) {
        try{
            User user = staticUserDB.login(loginData.getUsername(), loginData.getPassword());
            return Response.ok(user).header("Authorization", createJWT(user)).build();
        }catch (NoSuchElementException e){
            return Response.status(Response.Status.UNAUTHORIZED).entity(e).build();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}