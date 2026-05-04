package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.RerolledItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.Objects;

@ApplicationScoped
public class PlayerRerollRepository extends CrudRepository<RerolledItem, Long> {
    public PlayerRerollRepository() {
        super(RerolledItem.class);
    }

    public Integer getRerollCount(EntityManager entityManager, Integer playerId, Long roundId) {
        Long count = entityManager
                .createQuery("""
                    select count(t) from RerolledItem t
                    where t.player.id = :playerId
                        and t.roundId = :roundId
                    """,
                        Long.class)
                .setParameter("playerId", playerId)
                .setParameter("roundId", roundId)
                .getSingleResultOrNull();

        return (Objects.isNull(count))? 0: count.intValue();
    }
}
