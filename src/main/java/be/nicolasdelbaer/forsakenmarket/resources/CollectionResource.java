package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.models.inventory.CollectionItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("/collection")
@Authenticated
@RolesAllowed("Merchant")
@Tag(name = "Collection", description = "Manage collection")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CollectionResource {

    private static final Logger log = LoggerFactory.getLogger(CollectionResource.class);
    @Inject private CollectionService collectionService;
    @Context private SecurityContext securityContext;

    @GET
    @Operation(summary = "Get collection items", description = "Fetch all blueprint item data available discovered by player")
    public Response getCollection(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            List<CollectionItemResponse> itemList = collectionService
                    .fetchCollection(playerSession.id());
            response = Response.ok(itemList).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}
