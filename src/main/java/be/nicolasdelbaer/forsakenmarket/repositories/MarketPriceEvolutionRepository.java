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

        List<Long> priceEvolutionIdList = entityManager.createQuery("""
                    select mpe.id from MarketPriceEvolution mpe
                    where mpe.itemBlueprint.id = :id
                    order by mpe.endRoundId DESC
                    """, Long.class)
                .setParameter("id", itemBlueprintId)
                .setMaxResults(50)
                .getResultList();

        if(priceEvolutionIdList.isEmpty()) return List.of();

        return entityManager.createQuery("""
                        select mpe from MarketPriceEvolution mpe
                        where mpe.id IN :ids
                        order by mpe.endRoundId ASC
                        """, MarketPriceEvolution.class)
                .setParameter("ids", priceEvolutionIdList)
                .getResultList();
    }

}
