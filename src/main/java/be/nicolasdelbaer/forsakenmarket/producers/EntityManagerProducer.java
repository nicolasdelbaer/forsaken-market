package be.nicolasdelbaer.forsakenmarket.producers;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class EntityManagerProducer {

    private EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void init() {
        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", System.getenv("DB_URL"));
        props.put("jakarta.persistence.jdbc.user", System.getenv("DB_USER"));
        props.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));
        entityManagerFactory = Persistence.createEntityManagerFactory(System.getenv("PERSISTENCE_UNIT_NAME"), props);
    }

    @Produces
    @ApplicationScoped
    public EntityManagerFactory produceEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager entityManager) {
        if (entityManager.isOpen()) entityManager.close();
    }

    @PreDestroy
    public void destroy() {
        if (entityManagerFactory.isOpen()) entityManagerFactory.close();
    }
}