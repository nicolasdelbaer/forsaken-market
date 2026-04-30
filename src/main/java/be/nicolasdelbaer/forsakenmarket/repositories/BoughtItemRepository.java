package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BoughtItemRepository extends CrudRepository<BoughtItem, Long> {
    public BoughtItemRepository() {
        super(BoughtItem.class);
    }

    public List<BoughtItem> fetchActiveItems(EntityManager entityManager) {
        return entityManager.createQuery("""
                    select bi from BoughtItem bi
                    where bi.status = :status
                    """, BoughtItem.class)
            .setParameter("status", MarketItemStatus.BOUGHT)
            .getResultList();
    }

    public Optional<BoughtItem> getItemFromPlayer(
            EntityManager entityManager, Long itemId, Integer playerId, MarketItemStatus status) {
        BoughtItem result = entityManager.createQuery("""
                    select bi from BoughtItem bi
                    where bi.id = :itemId
                        and bi.status = :status
                        and bi.player.id = :playerId
                    """, BoughtItem.class)
            .setParameter("itemId", itemId)
            .setParameter("playerId", playerId)
            .setParameter("status", status)
            .getSingleResultOrNull();
        return Optional.ofNullable(result);
    }
}
