package at.kaindorf.jwt;

import at.kaindorf.services.LoginResource;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACVerifier;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.annotation.Priority;
import java.io.IOException;
import java.nio.file.Watchable;
import java.text.ParseException;

import java.io.IOException;
import java.text.ParseException;

@JWTNeeded
@Provider
@Priority(Priorities.AUTHORIZATION)
public class JWTNeededFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext crc) throws IOException {
        String jwtToken = crc.getHeaderString("Authorization");
        jwtToken = jwtToken.replace("Bearer", "");
        try{
            JWSObject jwsObject = JWSObject.parse(jwtToken);
            boolean verified = jwsObject.verify(new MACVerifier(LoginResource.JWT_SECRET));
            //entweder es passt nicht
            if(!verified){
                throw new RuntimeException("not verified");
            }
            //oder man landet bei der Basket resource
            else{
                crc.setProperty("username", jwsObject.getPayload());
            }
        }catch (ParseException | JOSEException | RuntimeException e) {
            crc.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(e).build());
        }
    }
}

