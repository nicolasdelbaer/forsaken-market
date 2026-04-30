package be.nicolasdelbaer.forsakenmarket.filters;

import be.nicolasdelbaer.forsakenmarket.annotations.Public;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;
import java.util.List;

@ApplicationScoped
@Provider
public class AuthFilter implements ContainerRequestFilter {

    @Context private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path.equals("openapi.json") || path.equals("openapi.yaml"))
            return;

        boolean isPublic = resourceInfo.getResourceMethod().isAnnotationPresent(Public.class)
                || resourceInfo.getResourceClass().isAnnotationPresent(Public.class);
        if (isPublic) return;

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build()
            );
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = JwtUtils.getClaims(token);
            List<String> roles = JwtUtils.getRoles(claims, token);
            PlayerSession playerSession = new PlayerSession(
                    JwtUtils.getId(claims, token),
                    JwtUtils.getEmail(claims, token),
                    JwtUtils.getUsername(claims, token),
                    JwtUtils.getRoles(claims, token)
                    );

            /*
             * Security context to retrieve from endpoints with
             * @Context SecurityContext securityContext as param
             * then -> securityContext.getUserPrincipal()
             */
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
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).build()
            );
        }
    }
}
