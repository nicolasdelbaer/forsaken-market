package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.broadcaster.BroadcastEventQueue;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.ExpiredItemsBroadcast;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.TradeBroadcast;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import be.nicolasdelbaer.forsakenmarket.repositories.InventoryItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.services.PlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.ReputationCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ScheduledInventoryService {

    @Inject private InventoryItemRepository inventoryItemRepository;
    @Inject private GameStateManager gameStateManager;
    @Inject private EventBroadcaster eventBroadcaster;
    @Inject private BroadcastEventQueue broadcastEventQueue;

    @Inject private PlayerRepository playerRepository;
    @Inject private PlayerService playerService;

    /*
     * EntityManager is passed because of the scheduler scope
     * Decayed items cannot be sold anymore, updates will occur each round
     */
    public void updateExpirationTime(EntityManager entityManager) {
        List<InventoryItem> ownedItems = inventoryItemRepository.fetchBoughtItems(entityManager);
        Long currentRound = gameStateManager.getCurrentRound();

        Map<Integer, List<TradeBroadcast>> expiredItems = new HashMap<>();
        for (InventoryItem ownedItem : ownedItems) {
            ownedItem.updateExpiration(currentRound);

            if(ownedItem.isDecayed()) {
                MarketPrice marketPrice = gameStateManager
                        .getCurrentMarketPrice(ownedItem.getItemBlueprint().getId());

                ownedItem.setSoldPrice(marketPrice.getCurrentPrice());

                Player player = ownedItem.getPlayer();
                playerService.credit(player, marketPrice.getCurrentPrice());
                playerService.addReputation(
                        player,
                        ReputationCalculator.calculate(new ReputationScoreData(
                                ownedItem.getBoughtPrice(),
                                marketPrice.getCurrentPrice()))
                );
                playerRepository.update(entityManager, player);

                InventoryItem finalOwnedItem = inventoryItemRepository.update(entityManager, ownedItem);
                expiredItems.computeIfAbsent(
                        player.getId(),
                        ArrayList::new)
                    .add(new TradeBroadcast(
                            ownedItem.getId(),
                            marketPrice.getCurrentPrice(),
                            finalOwnedItem.getPriceDifference()
                    ));
            }
        }
        inventoryItemRepository.updateAll(entityManager, ownedItems);

        for (Map.Entry<Integer, List<TradeBroadcast>> entry : expiredItems.entrySet()) {
            broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToPlayer(BroadcastEvent.ItemExpiration,
                    new ExpiredItemsBroadcast(entry.getValue()),
            entry.getKey()
            ));
        }

    }
}
