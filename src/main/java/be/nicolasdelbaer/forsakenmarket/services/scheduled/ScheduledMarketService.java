package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPriceEvolution;
import be.nicolasdelbaer.forsakenmarket.enums.MarketTrend;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedBlueprintException;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketOHLC;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemBlueprintRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketItemRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketPriceEvolutionRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketPriceRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.MarketPriceUtils;
import be.nicolasdelbaer.forsakenmarket.utils.PricesCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;


/*
 * Executed by scheduler -> need to pass the entityManager to methods
 */
@ApplicationScoped
public class ScheduledMarketService {

    @Inject private MarketItemRepository marketItemRepository;
    @Inject private MarketPriceEvolutionRepository marketPriceEvolutionRepository;
    @Inject private GameStateManager gameStateManager;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;
    @Inject private MarketPriceRepository marketPriceRepository;


    /*
     * Market Refresh consists on:
     *   - adding new items to fill the gaps
     */
    public void refreshMarket(EntityManager entityManager, List<ItemBlueprint> blueprintRepositoryAll) {
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
        MarketItem marketItem = new MarketItem();
        marketItem.setItemBlueprint(itemBlueprint);
        marketItem.setCreatedRoundId(gameStateManager.getCurrentRound());
        marketItem.setTimeToLive(getTimeToLive());

        marketItemRepository.save(entityManager, marketItem);
    }

    //TODO get random value from proper class without magic numbers
    private static int getTimeToLive() {
        return RandomGenerator.getDefault().nextInt(3, 10);
    }

    /*
    * Calculate Open/High/close/Low prices for stock market display
    * Each @duration rounds, a new MarketPriceEvolution list by blueprints will be created
    */
    public void recordMarketPriceMovements(EntityManager entityManager, long startRoundId, int duration) throws UndefinedBlueprintException {
        List<MarketPriceEvolution> marketPriceEvolutionList = new ArrayList<>();
        List<MarketOHLC> priceEvolutionList = marketPriceRepository.getAllEvolutionData(entityManager, startRoundId, duration);

        for (MarketOHLC ohcl : priceEvolutionList) {
            ItemBlueprint bp = gameStateManager.getItemBlueprint(ohcl.item_blueprint_id());
            MarketPriceEvolution marketPriceEvolution = new MarketPriceEvolution();
            marketPriceEvolution.setOpen(ohcl.open()); //get last
            marketPriceEvolution.setClose(ohcl.close());
            marketPriceEvolution.setHigh(ohcl.high()); //fetch max price
            marketPriceEvolution.setLow(ohcl.low()); //fetch min price
            marketPriceEvolution.setItemBlueprint(bp);
            marketPriceEvolution.setStartRoundId(startRoundId);
            marketPriceEvolution.setEndRoundId(startRoundId+duration);
            marketPriceEvolutionList.add(marketPriceEvolution);
        }
        marketPriceEvolutionRepository.saveAll(entityManager, marketPriceEvolutionList);
    }

    private static MarketTrend getMarketTrend(MarketOHLC ohcl) {
        //TODO add logic for market trend - need to evaluate the need for this feature
        return MarketTrend.STABLE;
    }

    /*
     * Updates items duration on market.
     * When over they'll disappear from the available list and will have the expired status.
     */
    public void updateTimeToLive(EntityManager entityManager) {
        List<MarketItem> marketItemList = marketItemRepository
                .findAllByRoundId(entityManager);

        Long currentRound = gameStateManager.getCurrentRound();
        List<MarketItem> expiredList = new ArrayList<>();
        for (MarketItem marketItem : marketItemList) {
            marketItem.updateExpiration(currentRound);
            if(marketItem.isExpired()) expiredList.add(marketItem);
        }
        marketItemRepository.updateAll(entityManager, expiredList);

    }

    /*
     * Populate prices movement into db
     * Update prices in server cache for getting current prices
     */
    public void updateMarketPrices(EntityManager entityManager) {
        List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);

        Map<ItemBlueprint, PriceMovementByRound> updatedPrices = new HashMap<>();

        for (ItemBlueprint blueprint : blueprintList) {
            PriceMovementByRound priceMovementByRound = MarketPriceUtils.getMarketRoundMovement(gameStateManager, blueprint);
            PriceMovementByRound nextPrice = PricesCalculator.getNextPrice(blueprint, priceMovementByRound);
            updatedPrices.put(blueprint, nextPrice);
        }

        List<MarketPrice> prices = updatedPrices.entrySet().stream()
                .map(entry -> {
                    Integer newPrice = PricesCalculator.getNextPrice(
                            entry.getKey(),
                            entry.getValue()).currentPrice();

                    MarketPrice marketPrice = new MarketPrice();
                    marketPrice.setCurrentPrice(newPrice);
                    marketPrice.setItemBlueprint(entry.getKey());
                    marketPrice.setRoundId(gameStateManager.getCurrentRound());
                    return marketPrice;
                })
                .toList();

        marketPriceRepository.saveAll(entityManager, prices);

        //Refresh cached server data
        gameStateManager.setMarketPriceList(updatedPrices.entrySet().stream().collect(Collectors.toMap(
        entry -> entry.getKey().getId(),
        Map.Entry::getValue
        )));
    }




}
