package be.nicolasdelbaer.forsakenmarket.filters;

import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;
import be.nicolasdelbaer.forsakenmarket.models.ErrorDto;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ForsakenRuntimeExceptionMapper
        implements ExceptionMapper<ForsakenRuntimeException> {

    @Override
    public Response toResponse(ForsakenRuntimeException e) {
        return Response
                .status(422)
                .entity(new ErrorDto(e.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}