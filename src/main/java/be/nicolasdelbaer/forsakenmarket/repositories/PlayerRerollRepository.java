package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.PlayerReroll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerRerollRepository extends CrudRepository<PlayerReroll, Long> {
    public PlayerRerollRepository() {
        super(PlayerReroll.class);
    }

    public Integer getRerollCount(Long roundId) {
        Integer count = 0;
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()){
            count = entityManager
                    .createQuery("select count(t) from PlayerReroll t where t.roundId = :roundId group by t.roundId",
                            MarketPrice.class)
                    .setParameter("roundId", roundId)
                    .executeUpdate();
            return count;
        }
    }
}
