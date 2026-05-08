package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.models.admin.PlayerConnections;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/admin")
@Authenticated
@RolesAllowed("Admin")
@Produces(MediaType.APPLICATION_JSON)
public class AdminResource {
    @Inject private EventBroadcaster eventBroadcaster;

    @GET
    /**
     * Returns the number of active connections of each playerId
     */
    public Response displayThreads() {
        return Response
                .ok(eventBroadcaster.getConnectionsPeek().entrySet()
                        .stream()
                        .map(entry -> new PlayerConnections(
                                entry.getKey(),
                                entry.getValue()))
                        .toList())
                .build();
    }
}