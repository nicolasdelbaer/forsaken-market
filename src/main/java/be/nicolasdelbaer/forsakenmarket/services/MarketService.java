package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.entities.RerolledItem;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MarketItemDoesNotExistException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedBlueprintException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketBlueprintResponse;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketItemResponse;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketOHLCResponse;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.MarketPriceUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class MarketService {

    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceEvolutionRepository marketPriceEvolutionRepository;
    @Inject private PlayerRerollRepository playerRerollRepository;

    @Inject private GameStateManager gameStateManager;
    @Inject private PlayerRepository playerRepository;

    @Inject private EntityManager entityManager;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;

    @Transactional
    public void rerollItem(Integer playerId, Long itemId)
            throws PlayerInsufficientFundsException, MaxRerollReachedException, MarketItemDoesNotExistException, PlayerNotFoundException {
        //Note, the current round id is resolved here for keeping coherence
        Long currentRound = gameStateManager.getCurrentRound();
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
        rerolledItem.setRoundId(currentRound);
        playerRerollRepository.save(entityManager, rerolledItem);
    }

    /*
     * Based on a player id, returns their available items excluding already rerolled or bought items from the available market list.
     */
    public List<MarketItemResponse> fetchAvailableItems(Integer playerId){
        return marketItemRepository
                .findAllValidItemsForPlayer(entityManager, gameStateManager.getCurrentRound(), playerId)
                .stream().map(
                    marketItem -> new MarketItemResponse(
                         marketItem.getId(),
                         marketItem.getItemBlueprint().getTitle(),
                         marketItem.getItemBlueprint().getDescription(),
                         marketItem.getItemBlueprint().getIcon(),
                         marketItem.getItemBlueprint().getRarity().name(),
                            MarketPriceUtils.getMarketRoundMovement(gameStateManager, marketItem.getItemBlueprint()).currentPrice()))
                .toList();
    }

    /*
     * Based on an itemblueprint id, returns the predefined amount of last OHLC prices.
     */
    public List<MarketOHLCResponse> getMarketEvolution(Long itemId) throws UndefinedBlueprintException {
        return marketPriceEvolutionRepository
                .findByBlueprint(entityManager, itemId, GameConfiguration.OHLC_range)
                .stream()
                .map(marketPriceEvolution ->
                    new MarketOHLCResponse(
                        marketPriceEvolution.getOpen(),
                        marketPriceEvolution.getHigh(),
                        marketPriceEvolution.getLow(),
                        marketPriceEvolution.getClose()
                ))
                .toList();
    }

    public List<MarketBlueprintResponse> fetchAvailableBlueprints() {
        return itemBlueprintRepository
                .findAll(entityManager)
                .stream().map(
                        blueprint -> new MarketBlueprintResponse(
                                blueprint.getId(),
                                blueprint.getTitle(),
                                blueprint.getDescription(),
                                blueprint.getIcon(),
                                blueprint.getRarity().name()))
                .toList();
    }
}
