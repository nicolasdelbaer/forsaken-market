package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.repositories.InventoryItemRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ScheduledInventoryService {

    @Inject private InventoryItemRepository inventoryItemRepository;
    @Inject private GameState gameState;

    /*
     * EntityManager is passed because of the scheduler scope
     * Decayed items cannot be sold anymore, updates will occur each round
     */
    public void updateDecay(EntityManager entityManager) {
        List<InventoryItem> ownedItems = inventoryItemRepository.fetchBoughtItems(entityManager);
        Long currentRound = gameState.getCurrentRound();
        for (InventoryItem ownedItem : ownedItems)
            ownedItem.updateExpiration(currentRound);
        inventoryItemRepository.updateAll(entityManager, ownedItems);

    }
}
