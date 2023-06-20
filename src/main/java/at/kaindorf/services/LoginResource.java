package at.kaindorf.services;

import at.kaindorf.beans.LoginData;
import at.kaindorf.beans.User;
import at.kaindorf.db.DB_Access;
import at.kaindorf.db.UserDB;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Path("/login")
public class LoginResource {
    private UserDB userDB = UserDB.getInstance();
    private DB_Access access = DB_Access.getInstance();
    public static final String JWT_SECRET ="this-is-my-not-very-long-should-be-much-much-longer-secret-yes-yes-yes";

    public LoginResource() throws SQLException, ClassNotFoundException {
    }

    private String createJWT(User user) throws JOSEException {
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256),
                new Payload(user.getUsername()));
        jwsObject.sign(new MACSigner(JWT_SECRET.getBytes()));
        System.out.println(jwsObject.serialize());
        return jwsObject.serialize();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginData loginData) {
        try{
            System.out.println(loginData);
            User user = userDB.login(loginData.getUsername(), loginData.getPwd());
            return Response.ok(user).header("Authorization", createJWT(user)).build();
        }catch (NoSuchElementException e){
            return Response.status(Response.Status.UNAUTHORIZED).entity(e).build();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    //insert new user
    @PUT
    public Response insertUser(User user){
        Optional<Object> userOptional = access.insertObject(user);
        userDB.insertUser(user);
        return Response.accepted(userOptional.get()).build();
    }

    @GET
    public Response getAllUsernames(){
        List<String> usernames = userDB.getAllUsernames();
        return Response.ok(usernames).build();
    }


}