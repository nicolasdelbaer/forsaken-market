package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.models.player.LeaderboardResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
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

@Path("/hall-of-infamy")
@Authenticated
@RolesAllowed("Merchant")
@Tag(name = "HallOfInfamy", description = "check player stats")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HallOfInfamyResource {

    private static final Logger log = LoggerFactory.getLogger(HallOfInfamyResource.class);
    @Context private SecurityContext securityContext;
    @Inject private PlayerService playerService;


    @GET
    @Path("/top25")
    @Operation(summary = "Get best 25 players", description = "Fetch players info for best 25 totReput score")
    public Response getItems(){
        Response response;
        PlayerSession playerSession = (PlayerSession) securityContext.getUserPrincipal();
        try {
            List<LeaderboardResponse> entries = playerService.fetchLeaderboard(25);
            response = Response.ok(entries).build();
        } catch (Exception e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.CannotRetrievePlayers).build();
            log.warn(e.getMessage(), e);
        }
        return response;
    }
}
