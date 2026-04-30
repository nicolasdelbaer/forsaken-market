package be.nicolasdelbaer.forsakenmarket.services;

import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketTrend;
import be.nicolasdelbaer.forsakenmarket.exceptions.BadItemOwnerException;
import be.nicolasdelbaer.forsakenmarket.exceptions.MaxRerollReachedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.models.inventory.BuyItemDto;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;

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
    public void rerollItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException, MaxRerollReachedException {
        Long currentRound = gameState.getCurrentRound();
        MarketItem itemInstance = marketItemRepository.findById(entityManager, itemId).orElseThrow();
        Player player = playerRepository.findById(entityManager, playerId).orElseThrow();

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

        //TODO update market view for player
    }

    @Transactional
    public void buyItem(Integer playerId, Long itemId) throws PlayerInsufficientFundsException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        //Retrieving items
        MarketItem itemInstance = marketItemRepository.findById(entityManager, itemId).orElseThrow();
        MarketPrice marketPrice = marketPriceRepository.findByBlueprint(entityManager, itemInstance.getItemBlueprint(), currentRound).orElseThrow();
        Player player = playerRepository.findById(entityManager, playerId).orElseThrow();

        //verify data integrity (current round, datetime request)
        //TODO check & user input
        // /!\ -> tolerance must not introduce exploits

        //remove player's money
        player.debit(marketPrice.getCurrentPrice());
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.addToInventory(new BuyItemDto(player, itemInstance, marketPrice, currentRound));
    }

    @Transactional
    public void sellItem(Integer playerId, Long itemId) throws BadItemOwnerException {
        //Note, the current round id is resolved here for keeping coherence
        // Idea -> could use a window of tolerance in the future allowing players to get the item even with lags
        Long currentRound = gameState.getCurrentRound();

        BoughtItem itemInstance = boughtItemRepository.findById(entityManager, itemId).orElseThrow();
        Player player = playerRepository.findById(entityManager, playerId).orElseThrow();

        //verify data integrity (current round, datetime request)
        //TODO check & user input
        // /!\ -> tolerance must not introduce exploits
        if(!itemInstance.getPlayer().equals(player)){
            throw new BadItemOwnerException("players doesn't correspond");
        }

        //Fetch current market price
        MarketPrice marketPrice = marketPriceRepository.findByBlueprint(entityManager, itemInstance.getItemBlueprint(), currentRound).orElseThrow();

        //remove player's money
        player.credit(marketPrice.getCurrentPrice());
        player.addReputation(10); //TODO calculate reputation score in proper class
        playerRepository.save(entityManager, player);

        //add item to inventory
        inventoryService.removeFromInventory(itemInstance, currentRound);
    }



    public List<MarketItem> fetchAvailableItems(Player player){
        List<MarketItem> results = new ArrayList<>();
        results = marketItemRepository.findAllValidItemsForPlayer(entityManager, gameState.getCurrentRound(), player.getId());
        return results;
    }

    /*
     * Executed by scheduler -> need to pass its entityManager
     * Market Refresh consists on:
     *   - adding new items to fill the gaps
     */
    public void refreshMarket(EntityManager entityManager) {
        System.out.println("Refreshing Market!");
        int count = marketItemRepository.getValidElementCount(entityManager);
        int nbItems = gameConfiguration.getMarketPoolSize() - count;
        for (int i = 0; i <nbItems; i++) {
            createNewItem(entityManager);
        }
    }

    private void createNewItem(EntityManager entityManager) {
        List<ItemBlueprint> blueprintRepositoryAll = itemBlueprintRepository.findAll(entityManager);
        Collections.shuffle(blueprintRepositoryAll);
        ItemBlueprint itemBlueprint = blueprintRepositoryAll.getFirst();

        List<MarketItem> marketItemList = new ArrayList<>();
        MarketItem marketItem = new MarketItem();
        marketItem.setCreatedAt(LocalDateTime.now());
        marketItem.setItemBlueprint(itemBlueprint);
        marketItem.setRoundId(gameState.getCurrentRound());
        marketItem.setTimeToLive(RandomGenerator.getDefault().nextInt(3,10)); //TODO get random value from proper class
        marketItemList.add(marketItem);

        marketItemRepository.saveAll(entityManager, marketItemList);
    }

    /*
    * Executed by scheduler -> need to pass its entityManager
    */
    public void updateMarketPrices(EntityManager entityManager) {
        //TODO query for getting all info to fill market price properly
        List<MarketPrice> marketPriceList = new ArrayList<>();
        List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);
        for (ItemBlueprint itemBlueprint : blueprintList) {
            MarketPrice marketPrice = new MarketPrice();
            marketPrice.setOpen(0); //get last
            marketPrice.setClose(itemBlueprint.getPrice());
            marketPrice.setHigh(itemBlueprint.getPrice()); //fetch max price
            marketPrice.setLow(0); //fetch min price
            marketPrice.setMarketTrend(MarketTrend.STABLE);
            marketPrice.setItemBlueprint(itemBlueprint);
            marketPrice.setRoundId(gameState.getCurrentRound());
            marketPrice.setCreatedAt(LocalDateTime.now());
            marketPriceList.add(marketPrice);
        }
        marketPriceRepository.saveAll(entityManager, marketPriceList);
    }

    /*
     * Executed by scheduler -> need to pass its entityManager
     */
    public void updateTimeToLive(EntityManager entityManager) {
        List<MarketItem> marketItemList = marketItemRepository
                .findAllByRoundId(entityManager);

        Long currentRound = gameState.getCurrentRound();
        List<MarketItem> expiredList = new ArrayList<>();
        for (MarketItem marketItem : marketItemList) {
            marketItem.updateExpiration(currentRound);
            if(marketItem.isExpired()) expiredList.add(marketItem);
        }
        marketItemRepository.updateAll(entityManager, expiredList);

    }
}
