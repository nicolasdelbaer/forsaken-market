package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MarketPriceRepository extends CrudRepository<MarketPrice, Long> {
    public MarketPriceRepository() {
        super(MarketPrice.class);
    }

    public Optional<MarketPrice> findByBlueprint(EntityManager entityManager, ItemBlueprint itemBlueprint, Long roundId) {
        MarketPrice marketPrice = null;
        marketPrice = entityManager
                .createQuery("""
                        select t from MarketPrice t
                        where t.itemBlueprint.id = :itemBlueprint
                            and t.roundId = :roundId
                        """, MarketPrice.class)
                .setParameter("itemBlueprint", itemBlueprint.getId())
                .setParameter("roundId", roundId)
                .getSingleResult();
        return Optional.of(marketPrice);
    }

    public List<MarketPrice> findByRoundId(EntityManager entityManager, Long roundId) {
        return entityManager.createQuery("""
                        select t from MarketPrice t
                        where t.roundId = :roundId
                    """, MarketPrice.class)
                .setParameter("roundId", roundId)
                .getResultList();

    }
}
