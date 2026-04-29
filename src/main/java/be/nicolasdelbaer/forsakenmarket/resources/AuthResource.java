package be.nicolasdelbaer.forsakenmarket.resources;

import be.nicolasdelbaer.forsakenmarket.annotations.Public;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerLoginException;
import be.nicolasdelbaer.forsakenmarket.models.player.LoginRequestDto;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.RegisterPlayerDto;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.JwtUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

@Path("/auth")
@RequestScoped
@Public
public class AuthResource {

    @Inject private PlayerService playerService;
    @Inject private Validator validator;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPlayer(RegisterPlayerDto registerPlayerDto){
        //Input validation
        Set<ConstraintViolation<RegisterPlayerDto>> violations = validator.validate(registerPlayerDto);
        if (!violations.isEmpty())
            return Response.status(400).entity(violations).build();

        //Data are validated
        Response response;
        try {
            PlayerResponse player = playerService.register(registerPlayerDto);
            response = Response.ok().build();
        } catch (EmailAlreadyUsedException e) {
            response = Response.status(400).entity(e.getMessage()).build();
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
            PlayerResponse player = playerService.login(loginRequestDto);
            String token = JwtUtils.generateToken(player);
            response = Response.ok(token).build();
        } catch (PlayerLoginException e) {
            response = Response.status(400).entity(e.getMessage()).build();
        }

        return response;
    }
}
