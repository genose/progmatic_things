package rest;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/hello")
@Tag(name = "Hello")
public class HelloWorld {

    @GET
    @ApiResponse(
            responseCode = "200",
            description = "Dire Bonjour Monde !!!"
    )
   @Produces(MediaType.TEXT_PLAIN)
    public String getHello() {
        // Return some cliched textual content
        return "<h1>Bonjour Monde !!!</h1>" ;
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello(@PathParam("name") String name) {
        return "Bonjour " + name ;
    }
}