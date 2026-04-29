package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Public;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/")
@Public
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Server is running!";
    }
}