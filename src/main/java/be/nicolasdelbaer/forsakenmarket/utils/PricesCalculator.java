package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.enums.ItemRarity;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;

import java.util.Map;
import java.util.random.RandomGenerator;


public class PricesCalculator {

    private static final Map<ItemRarity, PriceCoefficients> priceCoefficientsMap = Map.ofEntries(
        Map.entry(ItemRarity.MUNDANE, new PriceCoefficients(.10f, .15f, 0.05f)),
        Map.entry(ItemRarity.TAINTED, new PriceCoefficients(.15f, .10f, 0.15f)),
        Map.entry(ItemRarity.CURSED, new PriceCoefficients(.20f, .07f, 0.3f)),
        Map.entry(ItemRarity.FORSAKEN, new PriceCoefficients(.20f, .05f, 0.6f))
    );
    private static final int MIN_WARMUP = 5;
    private static final int MAX_WARMUP = 15;
    private static final int SHOCK_PERCENT_CHANCE = 5;

    /*
     * Calculate next price following price trends
     * More the price is far from the initial price, more it'll tend to reverse the trend
     * Shock market will add real chaos on a pure random basis
     * TODO: Other events will impact the price in the future
     */
    public static MarketPriceHistory getNextPrice(ItemBlueprint itemBlueprint, MarketPriceHistory priceHistory){
        PriceCoefficients priceCoefficients = priceCoefficientsMap.get(itemBlueprint.getRarity());
        float volatility = priceCoefficients.volatility();
        float momentumCoefficient = priceCoefficients.momentum();
        float reversionCoefficient = priceCoefficients.reversion();

        int current;
        int baseSell = itemBlueprint.getPrice();
        int momentum  = (int) ((priceHistory.currentPrice() - priceHistory.previousPrice()) * momentumCoefficient);
        int reversion = (int) ((baseSell - priceHistory.currentPrice()) * reversionCoefficient);
        int noise     = (int) ((Math.random() * 2 - 1) * volatility * baseSell);
        current = priceHistory.currentPrice() + momentum + reversion + noise;
        current += getMarketShockValue(baseSell);

        return new MarketPriceHistory(current, priceHistory.currentPrice());
    }

    /*
     * Make prices progress and simulate a scattered price start
     */
    public static MarketPriceHistory warmupPrice(ItemBlueprint itemBlueprint) {
        int warmupRounds = RandomGenerator.getDefault().nextInt(MIN_WARMUP, MAX_WARMUP);
        int baseSell = itemBlueprint.getPrice();
        MarketPriceHistory current = new MarketPriceHistory(baseSell, baseSell);

        for (int i = 0; i < warmupRounds; i++) {
            current = getNextPrice(itemBlueprint, current);
        }
        return current;
    }

    /*
     * Get price chaos to keep having surprises (good or bad)
     */
    private static int getMarketShockValue(int baseSell) {
        int shockValue = 0;
        boolean hasMarketShock = RandomGenerator.getDefault().nextInt(0, 100) <= SHOCK_PERCENT_CHANCE;
        if (hasMarketShock) {
            float direction = RandomGenerator.getDefault().nextBoolean() ? 1f : -1f;
            shockValue = (int) (baseSell * 0.15f * direction);
        }
        return shockValue;
    }
}
