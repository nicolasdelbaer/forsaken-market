package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.schedule.CannotUpdateDecayOnItemException;
import be.nicolasdelbaer.forsakenmarket.repositories.BoughtItemRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class ScheduledInventoryService {

    @Inject private BoughtItemRepository boughtItemRepository;
    @Inject private GameState gameState;

    /*
     * EntityManager is passed because of the scheduler scope
     * Decayed items cannot be sold anymore, updates will occur each round
     */
    public void updateDecay(EntityManager entityManager) throws CannotUpdateDecayOnItemException {
        List<BoughtItem> ownedItems = boughtItemRepository.fetchActiveItems(entityManager);
        Long currentRound = gameState.getCurrentRound();
        for (BoughtItem ownedItem : ownedItems) {
            if(ownedItem.getStatus() != MarketItemStatus.BOUGHT)
                throw new CannotUpdateDecayOnItemException("Cannot throw away a non decayed item");
            ownedItem.updateExpiration(currentRound);
        }
    }
}
