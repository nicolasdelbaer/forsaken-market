package be.nicolasdelbaer.forsakenmarket.filters;

import be.nicolasdelbaer.forsakenmarket.annotations.Authenticated;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;
import java.util.List;

@ApplicationScoped
@Provider
@Authenticated
public class AuthFilter implements ContainerRequestFilter {

    @Context private ResourceInfo resourceInfo;
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("openapi.json") || path.equals("openapi.yaml"))
            return;

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        String token = null;

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            Cookie cookie = requestContext.getCookies().get("auth_token");
            if(cookie != null) token = cookie.getValue();
        }else{
            token = authHeader.substring(7);
        }

        if(token == null){
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        try {
            Claims claims = JwtUtils.getClaims(token);
            if(!JwtUtils.isValid(claims)) throw new Exception();

            List<String> roles = JwtUtils.getRoles(claims, token);
            PlayerSession playerSession = getPlayerSession(claims, token);

            //Security context to retrieve from endpoints with
            //@Context SecurityContext securityContext as param
            //then -> securityContext.getUserPrincipal()
            SecurityContext securityContext = new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return playerSession;
                }
                @Override
                public boolean isUserInRole(String role) { return roles.contains(role); }
                @Override
                public boolean isSecure() { return requestContext.getSecurityContext().isSecure(); }
                @Override
                public String getAuthenticationScheme() { return "Bearer"; }
            };

            requestContext.setSecurityContext(securityContext);

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private static PlayerSession getPlayerSession(Claims claims, String token) {
        return new PlayerSession(
                JwtUtils.getId(claims, token),
                JwtUtils.getEmail(claims, token),
                JwtUtils.getUsername(claims, token),
                JwtUtils.getRoles(claims, token)
        );
    }
}
