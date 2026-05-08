package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemRequest;
import be.nicolasdelbaer.forsakenmarket.models.inventory.InventoryItemResponse;
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

    /*
     * Buy action from the market and add the item to a player's inventory
     * The player won't see the item anymore, but others will be able to buy the item too
     * The market items are shared and non-exclusive for all players
     */
    @Transactional
    public InventoryItem acquireItem(BuyItemRequest buyItemRequest){
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setItemBlueprint(buyItemRequest.marketItem().getItemBlueprint());
        inventoryItem.setMarketItemId(buyItemRequest.marketItem().getId());
        inventoryItem.setPlayer(buyItemRequest.player());

        inventoryItem.setBoughtPrice(buyItemRequest.marketPrice().getCurrentPrice());
        inventoryItem.setBoughtAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.BOUGHT);

        inventoryItem.setDecayNbRounds(getDecayTime());
        inventoryItem.setBoughtRoundId(buyItemRequest.roundId());

        inventoryItemRepository.save(entityManager, inventoryItem);
        return inventoryItem;
    }

    //TODO calculate right round nb time
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
    public void discardItem(Integer playerId, Long inventoryItemId) throws CannotDiscardItemException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameStateManager.getCurrentRound();

        InventoryItem inventoryItem = inventoryItemRepository
                .getItemFromPlayer(entityManager, inventoryItemId, playerId, List.of(MarketItemStatus.DECAYED))
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
                            .ofNullable(gameStateManager.getCurrentMarketPrice(inventoryItem.getItemBlueprint().getId()))
                            .orElseThrow(() -> new MarketPriceNotFoundException("No price found for blueprint"));

                    return new InventoryItemResponse(
                            inventoryItem.getId(),
                            inventoryItem.getItemBlueprint().getId(),
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

    public boolean hasAlreadyBought(Integer playerId, Long marketItemId) {
        return inventoryItemRepository
                .possessItem(entityManager, playerId, marketItemId, MarketItemStatus.BOUGHT);
    }

    public boolean isItemActive(Integer playerId, Long inventoryItemId) {
        return inventoryItemRepository
                .isAvailable(entityManager, inventoryItemId, playerId, List.of(MarketItemStatus.BOUGHT, MarketItemStatus.DECAYED));
    }
}
