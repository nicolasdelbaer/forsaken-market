package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPriceEvolution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MarketPriceEvolutionRepository extends CrudRepository<MarketPriceEvolution, Long> {
    public MarketPriceEvolutionRepository() {
        super(MarketPriceEvolution.class);
    }

    public Optional<MarketPriceEvolution> findByBlueprint(EntityManager entityManager, ItemBlueprint itemBlueprint, Long endRoundId) {
        MarketPriceEvolution marketPriceEvolution = entityManager.createQuery("""
                        select t from MarketPriceEvolution t
                        where t.itemBlueprint.id = :itemBlueprint
                            and t.endRoundId = :endRoundId
                        """, MarketPriceEvolution.class)
                .setParameter("itemBlueprint", itemBlueprint.getId())
                .setParameter("endRoundId", endRoundId)
                .getSingleResult();
        return Optional.of(marketPriceEvolution);
    }

    public List<MarketPriceEvolution> findByRoundId(EntityManager entityManager, Long endRoundId) {
        return entityManager.createQuery("""
                        select t from MarketPriceEvolution t
                        where t.endRoundId = :endRoundId
                    """, MarketPriceEvolution.class)
                .setParameter("endRoundId", endRoundId)
                .getResultList();

    }
}
