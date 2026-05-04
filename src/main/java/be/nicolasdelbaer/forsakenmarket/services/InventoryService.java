package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.CannotDiscardItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.models.inventory.InventoryItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;
import be.nicolasdelbaer.forsakenmarket.repositories.BoughtItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketPriceRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import be.nicolasdelbaer.forsakenmarket.utils.MarketPriceUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@ApplicationScoped
public class InventoryService {

    @Inject private BoughtItemRepository boughtItemRepository;
    @Inject private EntityManager entityManager;
    @Inject private GameState gameState;
    @Inject
    private MarketPriceRepository marketPriceRepository;

    /*
     * Buy action from the market and add the item to a player's inventory
     * The player won't see the item anymore, but others will be able to buy the item too
     * The market items are shared and non-exclusive for all players
     */
    @Transactional
    public void acquireItem(BuyItemDto buyItemDto){


        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setItemBlueprint(buyItemDto.marketItem().getItemBlueprint());
        inventoryItem.setMarketItemId(buyItemDto.marketItem().getId());
        inventoryItem.setPlayer(buyItemDto.player());

        inventoryItem.setBoughtPrice(buyItemDto.marketPrice().currentPrice());
        inventoryItem.setBoughtAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.BOUGHT);

        inventoryItem.setDecayNbRounds(getDecayTime());
        inventoryItem.setBoughtRoundId(buyItemDto.roundId());

        boughtItemRepository.save(entityManager, inventoryItem);
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
        boughtItemRepository.update(entityManager, inventoryItem);
    }

    /*
     * When an item is decayed you have to thrown it away, you cannot sell it anymore and it'll occupy an inventory slot
     */
    @Transactional
    public void discardItem(Integer playerId, Long itemId) throws CannotDiscardItemException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        InventoryItem inventoryItem = boughtItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.DECAYED)
                .orElseThrow(() -> new CannotDiscardItemException("Invalid item or unauthorized access"));

        inventoryItem.setDiscardedAt(LocalDateTime.now());
        inventoryItem.setStatus(MarketItemStatus.DISCARDED);
        inventoryItem.setDiscardedRoundId(currentRound);
        boughtItemRepository.update(entityManager, inventoryItem);
    }

    /*
     * Fetch all bought & decayed items
     */
    @Transactional
    public List<InventoryItemResponse> fetchItems(Integer playerId) throws MarketPriceNotFoundException {
        return boughtItemRepository.fetchAvailableItemsForPlayer(entityManager, playerId)
                .stream()
                .map(boughtItem -> {
                    MarketPriceHistory marketPriceHistory = Optional
                            .ofNullable(MarketPriceUtils.getMarketPriceHistory(gameState, boughtItem.getItemBlueprint()))
                            .orElseThrow(() -> new MarketPriceNotFoundException("No price found for blueprint"));

                    return new InventoryItemResponse(
                            boughtItem.getId(),
                            boughtItem.getItemBlueprint().getTitle(),
                            boughtItem.getItemBlueprint().getDescription(),
                            boughtItem.getItemBlueprint().getIcon(),
                            boughtItem.getItemBlueprint().getRarity().name(),
                            boughtItem.getBoughtPrice(),
                            marketPriceHistory.currentPrice(),
                            boughtItem.isDecayed()
                    );
                }).toList();
    }
}
