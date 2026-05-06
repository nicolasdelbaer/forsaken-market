package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.exceptions.auth.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.MissingEnvConfigurationException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerLoginException;
import be.nicolasdelbaer.forsakenmarket.models.player.LoginRequestDto;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.models.player.RegisterPlayerRequest;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import be.nicolasdelbaer.forsakenmarket.utils.JwtUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Path("/auth")
@RequestScoped
@Tag(name = "Auth", description = "Manage users")
public class AuthResource {

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);
    @Inject private PlayerService playerService;
    @Inject private Validator validator;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPlayer(RegisterPlayerRequest registerPlayerRequest){
        //Input validation
        Set<ConstraintViolation<RegisterPlayerRequest>> violations = validator.validate(registerPlayerRequest);
        if (!violations.isEmpty())
            return Response.status(400).entity(violations).build();

        //Data are validated
        Response response;
        try {
            playerService.register(registerPlayerRequest);
            response = Response.ok().build();
        } catch (EmailAlreadyUsedException e) {
            response = Response.status(400).entity(BadResponseUtils.AlreadyUsedEmail).build();
        } catch (MissingEnvConfigurationException | NumberFormatException e) {
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(BadResponseUtils.MissingEnvConfiguration).build();
        }

        return response;
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDto loginRequestDto){
        //Input validation
        Set<ConstraintViolation<LoginRequestDto>> violations = validator.validate(loginRequestDto);
        if (!violations.isEmpty())
            return Response.status(400).entity(violations).build();

        //Data are validated
        Response response;
        try {
            PlayerSession player = playerService.login(loginRequestDto);
            String token = JwtUtils.generateToken(player);
            response = Response.ok(token).build();
        } catch (PlayerLoginException e) {
            response = Response.status(Response.Status.BAD_REQUEST.getStatusCode(), BadResponseUtils.WrongLoginOrPass).build();
        }

        return response;
    }
}
