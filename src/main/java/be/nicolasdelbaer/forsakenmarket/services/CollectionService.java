package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.broadcaster.BroadcastEventQueue;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.entities.CollectionItem;
import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.CollectedItemBroadcast;
import be.nicolasdelbaer.forsakenmarket.repositories.CollectionItemRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

@ApplicationScoped
public class CollectionService {
    @Inject private GameStateManager gameStateManager;
    @Inject private EntityManager entityManager;
    @Inject private EventBroadcaster eventBroadcaster;
    @Inject private BroadcastEventQueue broadcastEventQueue;

    @Inject private CollectionItemRepository collectionItemRepository;

    public boolean isNewDiscovery(Integer playerId, Long id) {
        return !collectionItemRepository.isCollected(entityManager, playerId, id);
    }

    public void addToCollection(Player player, MarketItem itemInstance) {
        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setItemBlueprint(itemInstance.getItemBlueprint());
        collectionItem.setPlayer(player);
        collectionItem.setFoundAt(LocalDateTime.now());
        collectionItem.setFoundRoundId(gameStateManager.getCurrentRound());
        collectionItemRepository.save(entityManager, collectionItem);

        ItemBlueprint blueprint = itemInstance.getItemBlueprint();

        broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToPlayer(
                BroadcastEvent.NewCollectedItem,
                new CollectedItemBroadcast(
                        blueprint.getId(),
                        blueprint.getTitle(),
                        blueprint.getDescription(),
                        blueprint.getRarity().name()
                ), player.getId())
        );
    }
}
