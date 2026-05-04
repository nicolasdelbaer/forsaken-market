package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MarketItemRepository extends CrudRepository<MarketItem, Long> {
    public MarketItemRepository() {
        super(MarketItem.class);
    }

    public List<MarketItem> findAllByRoundId(EntityManager entityManager) {
        return entityManager
                .createQuery("""
                        select t from MarketItem t
                        where t.expired = false
                       \s""",
                        MarketItem.class)
                .getResultList();
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
        return entityManager
                .createQuery("""
                      select t from MarketItem t
                      where t.expired = false
                      and not exists (
                          select bi from BoughtItem bi
                          where bi.marketItemId = t.id
                          and bi.player.id = :playerId
                      )
                      and not exists (
                          select ri from RerolledItem ri
                          where ri.marketItem.id = t.id
                          and ri.player.id = :playerId
                      )
                      order by createdAt limit :itemLimit
                      """,
                        MarketItem.class)
                .setParameter("playerId", playerId)
                .setParameter("itemLimit", GameConfiguration.marketVisibleLimit)
                .getResultList();
    }
}
