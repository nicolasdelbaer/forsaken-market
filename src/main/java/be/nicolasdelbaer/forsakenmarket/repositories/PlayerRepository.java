package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.Player;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerRepository extends CrudRepository<Player, Integer> {
    public PlayerRepository() {
        super(Player.class);
    }

    public boolean emailExists(String email) {
        boolean exists = false;
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            exists = !entityManager.createQuery("select 1 from Player t where t.email = :email", Integer.class)
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getResultList()
                    .isEmpty();
            return exists;
        }
    }
}
