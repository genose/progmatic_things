package security;


import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

import static security.Tokened.TOKEN;

@Provider
@Tokened
@Priority(Priorities.AUTHENTICATION)
public class TokenedFilter implements ContainerRequestFilter {


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Récupère la clef Token dans le header
        String requestToken = requestContext.getHeaderString(TOKEN);
        Token token = new Token(requestToken);

        if (!token.isValide())
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }
}