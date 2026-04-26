package be.nicolasdelbaer.forsakenmarket.utils;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntityFactory {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getInstance() {
        Dotenv dotenv = Dotenv.load();

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url", dotenv.get("DB_URL"));
        props.put("jakarta.persistence.jdbc.user", dotenv.get("DB_USER"));
        props.put("jakarta.persistence.jdbc.password", dotenv.get("DB_PASSWORD"));

        if(Objects.isNull(emf))
            emf = Persistence.createEntityManagerFactory("forsakenPU", props);

        return emf;
    }
}
