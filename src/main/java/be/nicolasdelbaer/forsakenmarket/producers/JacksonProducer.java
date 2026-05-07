package be.nicolasdelbaer.forsakenmarket.producers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;


@ApplicationScoped
public class JacksonProducer {
    @Produces
    @Dependent
    public ObjectMapper objectMapper(){
        return new ObjectMapper().registerModules();
    }
}
