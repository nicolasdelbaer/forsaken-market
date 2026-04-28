package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.exceptions.BadItemOwnerException;
import be.nicolasdelbaer.forsakenmarket.exceptions.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

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

    public List<MarketItem> fetchAvailableItems(Player player){
        List<MarketItem> results = new ArrayList<>();
        //TODO return elements not rerolled
        return results;
    }

    public void rerollItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException, MaxRerollReachedException {
        Long currentRound = gameState.getCurrentRound();
        MarketItem itemInstance = marketItemRepository.findById(itemId).orElseThrow();
        Player player = playerRepository.findById(playerId).orElseThrow();

        //cannot reroll if you've already used all available rerolls for the current round
        Integer nbReroll = playerRerollRepository.getRerollCount(currentRound);
        if(nbReroll >= gameConfiguration.getMaxRerollsPerRound())
            throw new MaxRerollReachedException("too many rerolls for this round");

        //remove player's money
        player.debit(gameConfiguration.getRerollCost()); //TODO calculate the reroll price from dedicated static thresolds
        playerRepository.save(player);

        PlayerReroll playerReroll = new PlayerReroll();
        playerReroll.setMarketItem(itemInstance);
        playerReroll.setPlayer(player);
        playerReroll.setRerolledAt(LocalDateTime.now());
        playerReroll.setRoundId(currentRound);
        playerRerollRepository.save(playerReroll);

        //TODO update market view for player
    }

    public void buyItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        //Retrieving items
        MarketItem itemInstance = marketItemRepository.findById(itemId).orElseThrow();
        MarketPrice marketPrice = marketPriceRepository.findByBlueprint(itemInstance.getItemBlueprint(), currentRound).orElseThrow();
        Player player = playerRepository.findById(playerId).orElseThrow();

        //verify data integrity (current round, datetime request)
        //TODO check & user input
        // /!\ -> tolerance must not introduce exploits

        //remove player's money
        player.debit(marketPrice.getCurrentPrice());
        playerRepository.save(player);

        //add item to inventory
        inventoryService.addToInventory(new BuyItemDto(player, itemInstance, marketPrice, currentRound));
    }

    public void sellItem(Integer playerId, Long itemId) throws BadItemOwnerException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        BoughtItem itemInstance = boughtItemRepository.findById(itemId).orElseThrow();
        Player player = playerRepository.findById(playerId).orElseThrow();

        //verify data integrity (current round, datetime request)
        //TODO check & user input
        // /!\ -> tolerance must not introduce exploits
        if(!itemInstance.getPlayer().equals(player)){
            throw new BadItemOwnerException("players doesn't correspond");
        }

        //Fetch current market price
        MarketPrice marketPrice = marketPriceRepository.findByBlueprint(itemInstance.getItemBlueprint(), currentRound).orElseThrow();

        //remove player's money
        player.credit(marketPrice.getCurrentPrice());
        player.addReputation(10); //TODO calculate reputation score in util class
        playerRepository.save(player);

        //add item to inventory
        inventoryService.removeFromInventory(itemInstance, currentRound);
    }

    /* Market Refresh consists on:
     *   - updating items ttl & prices
     *   - removing invalid items
     *   - adding new items to fill the gaps
     */
    public void refreshMarket() {
        System.out.println("Refreshing Market!");
        //TODO implementation
    }
}
