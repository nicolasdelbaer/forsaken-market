package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.CollectionNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItem;
import be.nicolasdelbaer.forsakenmarket.models.inventory.CollectionItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.inventory.InventoryItemResponse;
import be.nicolasdelbaer.forsakenmarket.repositories.CollectionItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.InventoryItemRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@ApplicationScoped
public class InventoryService {

    @Inject private InventoryItemRepository inventoryItemRepository;
    @Inject private EntityManager entityManager;
    @Inject private GameStateManager gameStateManager;
    @Inject private CollectionItemRepository collectionItemRepository;

    /*
     * Buy action from the market and add the item to a player's inventory
     * The player won't see the item anymore, but others will be able to buy the item too
     * The market items are shared and non-exclusive for all players
     */
    @Transactional
    public void acquireItem(BuyItem buyItem){
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setItemBlueprint(buyItem.marketItem().getItemBlueprint());
        inventoryItem.setMarketItemId(buyItem.marketItem().getId());
        inventoryItem.setPlayer(buyItem.player());

        inventoryItem.setBoughtPrice(buyItem.marketPrice().getCurrentPrice());
        inventoryItem.setBoughtAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.BOUGHT);

        inventoryItem.setDecayNbRounds(getDecayTime());
        inventoryItem.setBoughtRoundId(buyItem.roundId());

        inventoryItemRepository.save(entityManager, inventoryItem);
    }

    //TODO calculte right round nb time
    private Integer getDecayTime() {
        return RandomGenerator.getDefault().nextInt(6, 16);
    }

    /*
     * Active bought items must be sold to get added value before they get decayed
     */
    @Transactional
    public void sellItem(InventoryItem inventoryItem, Long currentRound) throws CannotSellInactiveItemException {
        if(inventoryItem.getStatus() != MarketItemStatus.BOUGHT)
            throw new CannotSellInactiveItemException("The item is already decayed, sold or thrown away");
        inventoryItem.setSoldAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.SOLD);
        inventoryItem.setSoldRoundId(currentRound);
        inventoryItemRepository.update(entityManager, inventoryItem);
    }

    /*
     * When an item is decayed you have to thrown it away, you cannot sell it anymore and it'll occupy an inventory slot
     */
    @Transactional
    public void discardItem(Integer playerId, Long itemId) throws CannotDiscardItemException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameStateManager.getCurrentRound();

        InventoryItem inventoryItem = inventoryItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.DECAYED)
                .orElseThrow(() -> new CannotDiscardItemException("Invalid item or unauthorized access"));

        inventoryItem.setDiscardedAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.DISCARDED);
        inventoryItem.setDiscardedRoundId(currentRound);
        inventoryItemRepository.update(entityManager, inventoryItem);
    }

    /*
     * Fetch all bought & decayed items
     */
    @Transactional
    public List<InventoryItemResponse> fetchItems(Integer playerId) throws MarketPriceNotFoundException {
        return inventoryItemRepository.fetchAvailableItemsForPlayer(entityManager, playerId)
                .stream()
                .map(inventoryItem -> {
                    MarketPrice marketPrice = Optional
                            .ofNullable(gameStateManager.getPriceHistory(inventoryItem.getItemBlueprint().getId()))
                            .orElseThrow(() -> new MarketPriceNotFoundException("No price found for blueprint"));

                    return new InventoryItemResponse(
                            inventoryItem.getId(),
                            inventoryItem.getItemBlueprint().getTitle(),
                            inventoryItem.getItemBlueprint().getDescription(),
                            inventoryItem.getItemBlueprint().getIcon(),
                            inventoryItem.getItemBlueprint().getRarity().name(),
                            inventoryItem.getBoughtPrice(),
                            marketPrice.getCurrentPrice(),
                            inventoryItem.isDecayed()
                    );
                }).toList();
    }

    /*
     * Fetch Collection status
     * Get all blueprints data + if they've been find or not
     */
    @Transactional
    public List<CollectionItemResponse> fetchCollection(Integer playerId) throws CollectionNotFoundException {
        return collectionItemRepository.findAllForPlayer(entityManager, playerId);
    }
}
