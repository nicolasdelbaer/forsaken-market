package be.nicolasdelbaer.forsakenmarket.filters;

import be.nicolasdelbaer.forsakenmarket.annotations.Public;
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

        String token = authHeader.substring(7);

        try {
            Claims claims = JwtUtils.getClaims(token);
            //String name = JwtUtils.getUsername(claims, token);
            //String email = JwtUtils.getEmail(claims, token);
            List<String> roles = JwtUtils.getRoles(claims, token);
            String id = String.valueOf(JwtUtils.getId(claims, token));

            SecurityContext securityContext = new SecurityContext() {
                @Override
                public Principal getUserPrincipal() {
                    return () -> id;
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
