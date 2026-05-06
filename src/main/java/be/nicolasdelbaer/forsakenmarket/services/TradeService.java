package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItem;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import be.nicolasdelbaer.forsakenmarket.repositories.CollectionItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.InventoryItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.utils.BadResponseUtils;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.ReputationCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

@ApplicationScoped
public class TradeService {

    @Inject private InventoryItemRepository inventoryItemRepository;
    @Inject private MarketItemRepository marketItemRepository;

    @Inject private InventoryService inventoryService;
    @Inject private GameStateManager gameStateManager;
    @Inject private PlayerRepository playerRepository;
    @Inject private CollectionItemRepository collectionItemRepository;

    @Inject private EntityManager entityManager;

    @Transactional
    public void buyItem(Integer playerId, Long itemId)
            throws PlayerInsufficientFundsException, MarketItemDoesNotExistException, PlayerNotFoundException, MarketPriceNotFoundException, UndefinedMarketPriceException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameStateManager.getCurrentRound();

        //Retrieving items
        MarketItem itemInstance = marketItemRepository
                .findById(entityManager, itemId)
                .orElseThrow(() -> new MarketItemDoesNotExistException("Item not found"));
        MarketPrice marketPrice = gameStateManager.getPriceHistory(itemInstance.getItemBlueprint().getId());
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));

        //remove player's money
        player.debit(marketPrice.getCurrentPrice());
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.acquireItem(new BuyItem(player, itemInstance, marketPrice, currentRound));


        if(!collectionItemRepository.isCollected(entityManager, playerId, itemInstance.getItemBlueprint().getId())) {
            CollectionItem collectionItem = new CollectionItem();
            collectionItem.setItemBlueprint(itemInstance.getItemBlueprint());
            collectionItem.setPlayer(player);
            collectionItem.setFoundAt(LocalDateTime.now());
            collectionItem.setFoundRoundId(gameStateManager.getCurrentRound());
            collectionItemRepository.save(entityManager, collectionItem);
        }
    }

    @Transactional
    public void sellItem(Integer playerId, Long itemId)
            throws BadItemOwnershipException, CannotSellInactiveItemException, MarketPriceNotFoundException, PlayerNotFoundException, UndefinedMarketPriceException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameStateManager.getCurrentRound();

        InventoryItem itemInstance = inventoryItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.BOUGHT)
                .orElseThrow(() -> new BadItemOwnershipException(BadResponseUtils.InvalidItemOrUnauthorized));

        MarketPrice marketPrice = gameStateManager.getPriceHistory(itemInstance.getItemBlueprint().getId());

        //remove player's money
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException(BadResponseUtils.PlayerNotFound));
        player.credit(marketPrice.getCurrentPrice());
        player.addReputation(ReputationCalculator.calculate(new ReputationScoreData(
                itemInstance, marketPrice
        )));
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.sellItem(itemInstance, currentRound);
    }


}
