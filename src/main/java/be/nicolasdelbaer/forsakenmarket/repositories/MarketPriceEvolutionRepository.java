package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketPriceEvolution;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MarketPriceEvolutionRepository extends CrudRepository<MarketPriceEvolution, Long> {
    public MarketPriceEvolutionRepository() {
        super(MarketPriceEvolution.class);
    }

    public List<MarketPriceEvolution> findByBlueprint(EntityManager entityManager, long itemBlueprintId, int limit) {
        return entityManager.createQuery("""
                        select t from MarketPriceEvolution t
                        where t.itemBlueprint.id = :itemBlueprintId
                        order by t.endRoundId DESC
                        limit :limitMax
                        """, MarketPriceEvolution.class)
                .setParameter("itemBlueprintId", itemBlueprintId)
                .setParameter("limitMax", limit)
                .getResultList();
    }

}
