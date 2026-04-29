package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MarketItemRepository extends CrudRepository<MarketItem, Long> {
    public MarketItemRepository() {
        super(MarketItem.class);
    }

    public List<MarketItem> findAllByRoundId(EntityManager entityManager) {
        List<MarketItem> marketItemList = null;
        marketItemList = entityManager
                .createQuery("""
                        select t from MarketItem t
                        where t.expired = false
                       \s""",
                        MarketItem.class)
                .getResultList();
        return marketItemList;
    }

    //TODO
    public int getValidElementCount(EntityManager entityManager) {
        return entityManager
                .createQuery("""
                        select count(t) from MarketItem t
                        where t.expired = false
                        """,
                        Long.class)
                .getSingleResult()
                .intValue();
    }

    public List<MarketItem> findAllValidItemsForPlayer(EntityManager entityManager, Long roundId, Integer playerId) {
        List<MarketItem> marketItemList = null;
        marketItemList = entityManager
                .createQuery("""
                        select t from MarketItem t
                        left join BoughtItem bi on bi.player.id = :playerId AND bi.marketItemId = t.id
                        left join RerolledItem ri on ri.player.id = :playerId AND ri.marketItem.id = t.id
                        where t.roundId = :roundId
                            and t.expired = false
                            and bi.id is null
                            and ri.id is null
                        """,
                        MarketItem.class)
                .setParameter("roundId", roundId)
                .setParameter("playerId", playerId)
                .getResultList();
        return marketItemList;
    }
}
