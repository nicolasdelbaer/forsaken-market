package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.broadcaster.BroadcastEventQueue;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.AlreadyBoughtException;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.TradeBroadcast;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemRequest;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import be.nicolasdelbaer.forsakenmarket.repositories.InventoryItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.ReputationCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class TradeService {

    @Inject private GameStateManager gameStateManager;
    @Inject private EntityManager entityManager;
    @Inject private EventBroadcaster eventBroadcaster;
    @Inject private BroadcastEventQueue broadcastEventQueue;

    @Inject private InventoryItemRepository inventoryItemRepository;
    @Inject private MarketItemRepository marketItemRepository;
    @Inject private PlayerRepository playerRepository;

    @Inject private InventoryService inventoryService;
    @Inject private PlayerService playerService;
    @Inject private CollectionService collectionService;

    @Transactional
    public void buyItem(Integer playerId, Long marketItemId)
            throws AlreadyBoughtException, PlayerInsufficientFundsException, MarketItemDoesNotExistException, PlayerNotFoundException, MarketPriceNotFoundException, UndefinedMarketPriceException {

        if(inventoryService.hasAlreadyBought(playerId, marketItemId))
            throw new AlreadyBoughtException("player already bought this item");

        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameStateManager.getCurrentRound();

        //Retrieving items
        MarketItem marketItem = marketItemRepository
                .findById(entityManager, marketItemId)
                .orElseThrow(() -> new MarketItemDoesNotExistException("Item not found"));
        MarketPrice marketPrice = gameStateManager.getCurrentMarketPrice(marketItem.getItemBlueprint().getId());
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));

        //remove player's money
        playerService.debit(player, marketPrice.getCurrentPrice());
        playerRepository.update(entityManager, player);

        //add item to inventory
        InventoryItem itemInstance = inventoryService.acquireItem(new BuyItemRequest(player, marketItem, marketPrice, currentRound));

        if(collectionService.isNewDiscovery(playerId, marketItem.getItemBlueprint().getId())) {
            collectionService.addToCollection(player, marketItem);
        }
        broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToPlayer(
                BroadcastEvent.ItemBought,
                new TradeBroadcast(
                        itemInstance.getId(),
                        marketPrice.getCurrentPrice(),
                        itemInstance.getPriceDifference()
                ), playerId));
    }

    @Transactional
    public void sellItem(Integer playerId, Long inventoryItemId)
            throws BadItemOwnershipException, CannotSellInactiveItemException, MarketPriceNotFoundException, PlayerNotFoundException, UndefinedMarketPriceException {
        
        InventoryItem itemInstance = inventoryItemRepository
                .getItemFromPlayer(entityManager, inventoryItemId, playerId, List.of(MarketItemStatus.BOUGHT, MarketItemStatus.DECAYED))
                .orElseThrow(() -> new CannotSellInactiveItemException(BadResponseUtils.CannotSellItem));

        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameStateManager.getCurrentRound();

        MarketPrice marketPrice = gameStateManager.getCurrentMarketPrice(itemInstance.getItemBlueprint().getId());

        //remove player's money
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException(BadResponseUtils.PlayerNotFound));
        playerService.credit(player, marketPrice.getCurrentPrice());
        playerService.addReputation(
                player,
                ReputationCalculator.calculate(new ReputationScoreData(
                itemInstance.getBoughtPrice(),
                        marketPrice.getCurrentPrice()))
        );
        playerRepository.update(entityManager, player);

        //add item to inventory
        inventoryService.sellItem(itemInstance, currentRound);

        broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToPlayer(
                BroadcastEvent.ItemSold,
                new TradeBroadcast(
                        itemInstance.getId(),
                        marketPrice.getCurrentPrice(),
                        itemInstance.getPriceDifference()
                ), playerId));
    }


}
