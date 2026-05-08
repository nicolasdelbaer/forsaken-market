package be.nicolasdelbaer.forsakenmarket.repositories;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class InventoryItemRepository extends CrudRepository<InventoryItem, Long> {
    public InventoryItemRepository() {
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
            EntityManager entityManager, Long inventoryItemId, Integer playerId, List<MarketItemStatus> statusList) {
        InventoryItem result = entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.status IN (:statusList)
                        and bi.id = :inventoryItemId
                        and bi.player.id = :playerId
                    """, InventoryItem.class)
            .setParameter("inventoryItemId", inventoryItemId)
            .setParameter("statusList", statusList)
            .setParameter("playerId", playerId)
            .getSingleResultOrNull();
        return Optional.ofNullable(result);
    }

    public boolean possessItem(EntityManager entityManager, Integer playerId, Long marketItemId, MarketItemStatus status) {
        return entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.player.id = :playerId
                        and bi.marketItemId = :marketItemId
                        and bi.status = :status
                    """, InventoryItem.class)
                .setParameter("playerId", playerId)
                .setParameter("marketItemId", marketItemId)
                .setParameter("status", status)
                .getSingleResultOrNull() != null;
    }

    public boolean isAvailable(EntityManager entityManager, Long inventoryItemId, Integer playerId, List<MarketItemStatus> statusList) {
        return entityManager.createQuery("""
                    select bi from InventoryItem bi
                    where bi.id = :inventoryItemId
                        and bi.status IN (:statusList)
                        and bi.player.id = :playerId
                    """, InventoryItem.class)
                .setParameter("inventoryItemId", inventoryItemId)
                .setParameter("statusList", statusList)
                .setParameter("playerId", playerId)
                .getSingleResultOrNull() != null;
    }

}
