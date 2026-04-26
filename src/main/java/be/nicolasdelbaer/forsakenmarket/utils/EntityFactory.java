package be.nicolasdelbaer.forsakenmarket.utils;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityFactory {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getInstance() {
        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", System.getenv("DB_URL"));
        props.put("jakarta.persistence.jdbc.user", System.getenv("DB_USER"));
        props.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));

        if(Objects.isNull(emf))
            emf = Persistence.createEntityManagerFactory("forsakenPU", props);

        return emf;
    }
}
