package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.entities.*;
import be.nicolasdelbaer.forsakenmarket.enums.MarketTrend;
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
public class ScheduledMarketService {

    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceRepository marketPriceRepository;

    @Inject private GameState gameState;
    @Inject private GameConfiguration gameConfiguration;

    @Inject private ItemBlueprintRepository itemBlueprintRepository;


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

    /*
     * Executed by scheduler -> need to pass its entityManager
     */
    private void createNewItem(EntityManager entityManager) {
        List<ItemBlueprint> blueprintRepositoryAll = itemBlueprintRepository.findAll(entityManager);
        Collections.shuffle(blueprintRepositoryAll);
        ItemBlueprint itemBlueprint = blueprintRepositoryAll.getFirst();

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
