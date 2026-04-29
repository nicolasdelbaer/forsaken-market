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

    public Optional<MarketPrice> findByLastBlueprint(EntityManager entityManager, ItemBlueprint itemBlueprint) {
        MarketPrice marketPrice = null;
        marketPrice = entityManager
                .createQuery("select t from MarketPrice t where t.itemBlueprint = :itemBlueprint order by t.roundId DESC",
                        MarketPrice.class)
                .setParameter("itemBlueprint", itemBlueprint)
                .setMaxResults(1)
                .getSingleResult();
        return Optional.of(marketPrice);
    }

    public Optional<MarketPrice> findByBlueprint(EntityManager entityManager, ItemBlueprint itemBlueprint, Long roundId) {
        MarketPrice marketPrice = null;
        marketPrice = entityManager
                .createQuery("select t from MarketPrice t where t.itemBlueprint = :itemBlueprint and t.roundId = :roundId",
                        MarketPrice.class)
                .setParameter("itemBlueprint", itemBlueprint)
                .setParameter("roundId", roundId)
                .setMaxResults(1)
                .getSingleResult();
        return Optional.of(marketPrice);
    }

}
