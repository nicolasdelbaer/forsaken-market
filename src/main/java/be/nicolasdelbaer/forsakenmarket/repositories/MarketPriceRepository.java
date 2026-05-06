package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketOHLC;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MarketPriceRepository extends CrudRepository<MarketPrice, Long> {

    public MarketPriceRepository() {
        super(MarketPrice.class);
    }


    public List<MarketPrice> findByRoundId(EntityManager entityManager, Long roundId) {
        return entityManager.createQuery("""
                        select mp from MarketPrice mp
                        where mp.roundId = :roundId
                        """, MarketPrice.class)
                .setParameter("roundId", roundId)
                .getResultList();

    }

    public List<MarketOHLC> getAllEvolutionData(EntityManager entityManager, long startRoundId, int duration) {
        return entityManager
                .createNamedQuery("MarketPrice.ohlc", MarketOHLC.class)
                .setParameter("startId", startRoundId)
                .setParameter("endId", startRoundId+duration)
                .getResultList();
    }
}
