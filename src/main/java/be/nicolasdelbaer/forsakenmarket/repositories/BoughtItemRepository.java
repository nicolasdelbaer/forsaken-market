package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BoughtItemRepository extends CrudRepository<InventoryItem, Long> {
    public BoughtItemRepository() {
        super(InventoryItem.class);
    }

    public List<InventoryItem> fetchBoughtItems(EntityManager entityManager) {
        return entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.status = :status
                    """, InventoryItem.class)
            .setParameter("status", MarketItemStatus.BOUGHT)
            .getResultList();
    }

    public List<InventoryItem> fetchAvailableItemsForPlayer(EntityManager entityManager, Integer playerId) {
        return entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.status IN (:statusList)
                        and bi.player.id = :playerId
                    order by bi.boughtRoundId DESC
                    """, InventoryItem.class)
                .setParameter("statusList", List.of(MarketItemStatus.BOUGHT, MarketItemStatus.DECAYED))
                .setParameter("playerId", playerId)
                .getResultList();
    }

    public Optional<InventoryItem> getItemFromPlayer(
            EntityManager entityManager, Long itemId, Integer playerId, MarketItemStatus status) {
        InventoryItem result = entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.id = :itemId
                        and bi.status = :status
                        and bi.player.id = :playerId
                    """, InventoryItem.class)
            .setParameter("itemId", itemId)
            .setParameter("playerId", playerId)
            .setParameter("status", status)
            .getSingleResultOrNull();
        return Optional.ofNullable(result);
    }
}
