package be.nicolasdelbaer.forsakenmarket.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/")
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Server is running!";
    }
}