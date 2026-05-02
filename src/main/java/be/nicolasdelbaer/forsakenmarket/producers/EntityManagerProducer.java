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
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        String unit = System.getenv("PERSISTENCE_UNIT_NAME");

        //Fail fast check
        if (url == null) throw new IllegalStateException("DB_URL env var is missing");
        if (user == null) throw new IllegalStateException("DB_USER env var is missing");
        if (password == null) throw new IllegalStateException("DB_PASSWORD env var is missing");
        if (unit == null) throw new IllegalStateException("PERSISTENCE_UNIT_NAME env var is missing");

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", url);
        props.put("jakarta.persistence.jdbc.user", user);
        props.put("jakarta.persistence.jdbc.password", password);
        entityManagerFactory = Persistence.createEntityManagerFactory(unit, props);
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