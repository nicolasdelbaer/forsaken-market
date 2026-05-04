package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketItemStatus;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.exceptions.inventory.BadItemOwnershipException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.CannotSellInactiveItemException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketPriceNotFoundException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class MarketService {

    @Inject private BoughtItemRepository boughtItemRepository;
    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceRepository marketPriceRepository;
    @Inject private PlayerRerollRepository playerRerollRepository;

    @Inject private InventoryService inventoryService;
    @Inject private GameState gameState;
    @Inject private PlayerRepository playerRepository;

    @Inject private EntityManager entityManager;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;
    @Inject
    private PlayerService playerService;

    @Transactional
    public void rerollItem(Integer playerId, Long itemId)
            throws PlayerInsufficientFundsException, MaxRerollReachedException, MarketItemDoesNotExistException, PlayerNotFoundException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();
        MarketItem itemInstance = marketItemRepository.
                findById(entityManager, itemId)
                .orElseThrow(() -> new MarketItemDoesNotExistException("Item not found"));
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));

        //cannot reroll if you've already used all available rerolls for the current round
        Integer nbReroll = playerRerollRepository.getRerollCount(entityManager, player.getId(), currentRound);
        if(nbReroll >= GameConfiguration.maxRerollsPerRound)
            throw new MaxRerollReachedException("too many rerolls for this round");

        //remove player's money
        player.debit(GameConfiguration.rerollCost); //TODO calculate the reroll price from dedicated static thresolds
        playerRepository.save(entityManager, player);

        RerolledItem rerolledItem = new RerolledItem();
        rerolledItem.setMarketItem(itemInstance);
        rerolledItem.setPlayer(player);
        rerolledItem.setRerolledAt(LocalDateTime.now());
        rerolledItem.setRoundId(currentRound);
        playerRerollRepository.save(entityManager, rerolledItem);
    }

    @Transactional
    public void buyItem(Integer playerId, Long itemId)
            throws PlayerInsufficientFundsException, MarketItemDoesNotExistException, PlayerNotFoundException, MarketPriceNotFoundException, UndefinedMarketPriceException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();

        //Retrieving items
        MarketItem itemInstance = marketItemRepository
                .findById(entityManager, itemId)
                .orElseThrow(() -> new MarketItemDoesNotExistException("Item not found"));
        PriceMovementByRound marketPrice = gameState.getPriceHistory(itemInstance.getItemBlueprint().getId());
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException("player not found"));

        //remove player's money
        player.debit(marketPrice.currentPrice());
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.acquireItem(new BuyItemDto(player, itemInstance, marketPrice, currentRound));
    }

    @Transactional
    public void sellItem(Integer playerId, Long itemId)
            throws BadItemOwnershipException, CannotSellInactiveItemException, MarketPriceNotFoundException, PlayerNotFoundException, UndefinedMarketPriceException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameState.getCurrentRound();

        InventoryItem itemInstance = boughtItemRepository
                .getItemFromPlayer(entityManager, itemId, playerId, MarketItemStatus.BOUGHT)
                .orElseThrow(() -> new BadItemOwnershipException(BadResponseUtils.InvalidItemOrUnauthorized));

        PriceMovementByRound marketPrice = gameState.getPriceHistory(itemInstance.getItemBlueprint().getId());

        //remove player's money
        Player player = playerRepository
                .findById(entityManager, playerId)
                .orElseThrow(() -> new PlayerNotFoundException(BadResponseUtils.PlayerNotFound));
        player.credit(marketPrice.currentPrice());
        player.addReputation(ReputationCalculator.calculate(new ReputationScoreData(
                itemInstance, marketPrice
        )));
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.sellItem(itemInstance, currentRound);
    }


    /*
     * Based on a player id, returns their available items excluding already rerolled or bought items from the available market list.
     */
    public List<MarketItemResponse> fetchAvailableItems(Integer playerId){
        return marketItemRepository
                .findAllValidItemsForPlayer(entityManager, gameState.getCurrentRound(), playerId)
                .stream().map(
                    marketItem -> new MarketItemResponse(
                         marketItem.getId(),
                         marketItem.getItemBlueprint().getTitle(),
                         marketItem.getItemBlueprint().getDescription(),
                         marketItem.getItemBlueprint().getIcon(),
                         marketItem.getItemBlueprint().getRarity().name(),
                            MarketPriceUtils.getMarketRoundMovement(gameState, marketItem.getItemBlueprint()).currentPrice()))
                .toList();
    }

}
