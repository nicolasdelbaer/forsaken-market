package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.utils.EntityFactory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@ApplicationScoped
public class PlayerRepository extends CrudRepository<Player, Long> {
    public PlayerRepository() {
        super(Player.class);
    }

    public boolean emailExists(String email) {
        boolean exists = false;
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            exists = !entityManager.createQuery("select 1 from %s t where t.email = :email".formatted(entityName), Integer.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            return exists;
        }
    }
}
