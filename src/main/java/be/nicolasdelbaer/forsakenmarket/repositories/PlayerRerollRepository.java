package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.RerolledItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerRerollRepository extends CrudRepository<RerolledItem, Long> {
    public PlayerRerollRepository() {
        super(RerolledItem.class);
    }

    public Integer getRerollCount(EntityManager entityManager, Long roundId) {
        Integer count = 0;
        count = entityManager
                .createQuery("select count(t) from RerolledItem t where t.roundId = :roundId group by t.roundId",
                        MarketPrice.class)
                .setParameter("roundId", roundId)
                .executeUpdate();
        return count;
    }
}
