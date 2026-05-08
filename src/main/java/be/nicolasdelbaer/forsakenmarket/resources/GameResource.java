package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.models.game.GameStatusResponse;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/game")
@Authenticated
@RolesAllowed("Merchant")
@Tag(name = "Game", description = "Game state")
public class GameResource {

    @Inject private GameStateManager gameStateManager;

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get current game status", description = "Returns current round, time until next round, and cycle configuration")
    public Response status() {
        return Response.ok(new GameStatusResponse(
                gameStateManager.getCurrentRound(),
                gameStateManager.getSecondsUntilNextRound(),
                GameConfiguration.ROUND_DURATION_SECONDS,
                GameConfiguration.ROUNDS_BY_CYCLE
        )).build();
    }
}
