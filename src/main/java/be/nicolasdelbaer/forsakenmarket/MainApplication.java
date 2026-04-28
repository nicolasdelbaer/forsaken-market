package be.nicolasdelbaer.forsakenmarket;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class MainApplication extends ResourceConfig {
    public MainApplication() {
        packages("be.nicolasdelbaer.forsakenmarket");
        register(OpenApiResource.class);
    }
}