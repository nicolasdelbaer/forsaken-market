package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.*;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import be.nicolasdelbaer.forsakenmarket.utils.ReputationCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MarketService {

    @Inject private BoughtItemRepository boughtItemRepository;
    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceRepository marketPriceRepository;
    @Inject private PlayerRerollRepository playerRerollRepository;

    @Inject private InventoryService inventoryService;
    @Inject private GameState gameState;
    @Inject private GameConfiguration gameConfiguration;
    @Inject private PlayerRepository playerRepository;

    @Inject private EntityManager entityManager;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;

    @Transactional
    public void rerollItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException, MaxRerollReachedException, MarkeItemDoesNotExistException, PlayerNotFoundException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();
        MarketItem itemInstance = marketItemRepository.
                findById(entityManager, itemId)
                .orElseThrow(() -> new MarkeItemDoesNotExistException("Item not found"));;
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));;

        //cannot reroll if you've already used all available rerolls for the current round
        Integer nbReroll = playerRerollRepository.getRerollCount(entityManager, currentRound);
        if(nbReroll >= gameConfiguration.getMaxRerollsPerRound())
            throw new MaxRerollReachedException("too many rerolls for this round");

        //remove player's money
        player.debit(gameConfiguration.getRerollCost()); //TODO calculate the reroll price from dedicated static thresolds
        playerRepository.save(entityManager, player);

        RerolledItem rerolledItem = new RerolledItem();
        rerolledItem.setMarketItem(itemInstance);
        rerolledItem.setPlayer(player);
        rerolledItem.setRerolledAt(LocalDateTime.now());
        rerolledItem.setRoundId(currentRound);
        playerRerollRepository.save(entityManager, rerolledItem);
    }

    @Transactional
    public void buyItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException, MarkeItemDoesNotExistException, PlayerNotFoundException, MarketPriceNotFoundException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();

        //Retrieving items
        MarketItem itemInstance = marketItemRepository
                .findById(entityManager, itemId)
                .orElseThrow(() -> new MarkeItemDoesNotExistException("Item not found"));
        MarketPrice marketPrice = marketPriceRepository
                .findByBlueprint(entityManager, itemInstance.getItemBlueprint(), currentRound)
                .orElseThrow(() -> new MarketPriceNotFoundException("No price for item"));
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));

        //remove player's money
        player.debit(marketPrice.getCurrentPrice());
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.acquireItem(new BuyItemDto(player, itemInstance, marketPrice, currentRound));
    }

    @Transactional
    public void sellItem(Integer playerId, Long itemId) throws BadItemOwnershipException, CannotSellInactiveItemException, MarketPriceNotFoundException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();

        BoughtItem itemInstance = boughtItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.BOUGHT)
                .orElseThrow(() -> new BadItemOwnershipException("Invalid item or unauthorized access"));

        //Fetch current market price
        MarketPrice marketPrice = marketPriceRepository
                .findByBlueprint(entityManager, itemInstance.getItemBlueprint(), currentRound)
                .orElseThrow(() -> new MarketPriceNotFoundException("No price for item"));

        //remove player's money
        Player player = playerRepository.findById(entityManager, playerId).orElseThrow();
        player.credit(marketPrice.getCurrentPrice());
        player.addReputation(ReputationCalculator.calculate(new ReputationScoreData(
                itemInstance, marketPrice
        )));
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.sellItem(itemInstance, currentRound);
    }


    public List<MarketItem> fetchAvailableItems(Integer playerId){
        List<MarketItem> results = new ArrayList<>();
        results = marketItemRepository.findAllValidItemsForPlayer(entityManager, gameState.getCurrentRound(), playerId);
        return results;
    }
}
