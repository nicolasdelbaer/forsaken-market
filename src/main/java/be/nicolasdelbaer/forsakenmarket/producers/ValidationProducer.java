package be.nicolasdelbaer.forsakenmarket.producers;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ApplicationScoped
public class ValidationProducer {

    private ValidatorFactory validatorFactory;

    @PostConstruct
    public void init() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @Produces
    @ApplicationScoped
    public Validator produceValidator() {
        return validatorFactory.getValidator();
    }

    @PreDestroy
    public void destroy() {
        validatorFactory.close();
    }
}