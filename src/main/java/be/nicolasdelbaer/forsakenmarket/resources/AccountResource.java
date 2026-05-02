package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.models.player.PlayerInfoResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/account")
@Tag(name = "Player", description = "Player data")
public class AccountResource {
    private static final Logger log = LoggerFactory.getLogger(AccountResource.class);
    @Inject private PlayerService playerService;
    @Context private SecurityContext securityContext;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/info")
    @Operation(summary = "Get player data", description = "Get player data from session")
    public Response allItems(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            Integer wallet = playerService.getWallet(playerSession.id());
            response = Response.ok(new PlayerInfoResponse(playerSession.id(), playerSession.email(), playerSession.name(), wallet)).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), "").build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}
