package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.Player;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerRepository extends CrudRepository<Player, Integer> {
    public PlayerRepository() {
        super(Player.class);
    }

    public boolean emailExists(EntityManager entityManager, String email) {
        return !entityManager.createQuery("select 1 from Player t where t.email = :email", Integer.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }
    public Player findByEmail(EntityManager entityManager, String email) {
        return entityManager.createQuery("select t from Player t where t.email = :email", Player.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    public Integer getWallet(EntityManager entityManager, Integer playerId) {
        return entityManager.createQuery("""
                        select t.wallet from Player t
                        where t.id = :playerId
                        """, Integer.class)
                .setParameter("playerId", playerId)
                .getSingleResult();
    }
}
