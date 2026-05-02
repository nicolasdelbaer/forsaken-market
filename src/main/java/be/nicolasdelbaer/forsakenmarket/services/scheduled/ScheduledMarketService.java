package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketTrend;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;
import be.nicolasdelbaer.forsakenmarket.repositories.*;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import be.nicolasdelbaer.forsakenmarket.utils.PricesCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.random.RandomGenerator;


/*
 * Executed by scheduler -> need to pass the entityManager to methods
 */
@ApplicationScoped
public class ScheduledMarketService {

    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceRepository marketPriceRepository;

    @Inject private GameState gameState;

    @Inject private ItemBlueprintRepository itemBlueprintRepository;


    /*
     * Market Refresh consists on:
     *   - adding new items to fill the gaps
     */
    public void refreshMarket(EntityManager entityManager) {
        List<ItemBlueprint> blueprintRepositoryAll = itemBlueprintRepository.findAll(entityManager);
        int randomId;
        int count = marketItemRepository.getValidElementCount(entityManager);
        int nbItems = GameConfiguration.marketPoolSize - count;
        for (int i = 0; i <nbItems; i++) {
            randomId = RandomGenerator.getDefault().nextInt(blueprintRepositoryAll.size());
            createNewItem(entityManager, blueprintRepositoryAll.get(randomId));
        }
    }

    /*
     * Add a random new item from the item db to the market available items
     * They'll get an expiration time in rounds to get bought
     */
    private void createNewItem(EntityManager entityManager, ItemBlueprint itemBlueprint) {
        List<MarketItem> marketItemList = new ArrayList<>();
        MarketItem marketItem = new MarketItem();
        marketItem.setCreatedAt(LocalDateTime.now());
        marketItem.setItemBlueprint(itemBlueprint);
        marketItem.setCreatedRoundId(gameState.getCurrentRound());
        marketItem.setTimeToLive(RandomGenerator.getDefault().nextInt(3,10)); //TODO get random value from proper class
        marketItemList.add(marketItem);

        marketItemRepository.saveAll(entityManager, marketItemList);
    }

    /*
    * TODO market prices will express a full cycle of rounds
    */
    public void recordMarketPriceMovements(EntityManager entityManager) {
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
     * Updates items duration on market.
     * When over they'll disappear from the available list and will have the expired status.
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

    /*
     * Update prices in server cache based on previous price
     */
    public void updateMarketPrices(EntityManager entityManager) {
        Map<Long, MarketPriceHistory> marketPriceList = new HashMap<>();
        List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);

        for (ItemBlueprint blueprint : blueprintList) {
            marketPriceList.put(
                blueprint.getId(),
                PricesCalculator.getNextPrice(
                    blueprint,
                    gameState.getPriceHistory(blueprint.getId())
                )
            );
        }

        gameState.setPrices(marketPriceList);
    }
}
