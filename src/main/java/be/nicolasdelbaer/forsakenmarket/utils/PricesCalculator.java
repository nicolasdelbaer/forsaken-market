package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.enums.ItemRarity;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovement;

import java.util.Map;
import java.util.random.RandomGenerator;


public class PricesCalculator {

    private static final Map<ItemRarity, PriceCoefficients> priceCoefficientsMap = Map.ofEntries(
        Map.entry(ItemRarity.MUNDANE, new PriceCoefficients(.10f, .15f, 0.5f, 0.2f)),
        Map.entry(ItemRarity.TAINTED, new PriceCoefficients(.15f, .10f, 0.3f, 0.3f)),
        Map.entry(ItemRarity.CURSED, new PriceCoefficients(.20f, .07f, 0.175f, 0.2f)),
        Map.entry(ItemRarity.FORSAKEN, new PriceCoefficients(.20f, .05f, 0.15f, 0.1f))
    );
    private static final int MIN_WARMUP = 5;
    private static final int MAX_WARMUP = 15;
    private static final int SHOCK_PERCENT_CHANCE = 5;


    /*
     * Make prices progress and simulate a scattered price start
     */
    public static PriceMovement warmupPrice(ItemBlueprint itemBlueprint) {
        int warmupRounds = RandomGenerator.getDefault().nextInt(MIN_WARMUP, MAX_WARMUP);
        int baseSell = itemBlueprint.getPrice();
        PriceMovement current = new PriceMovement(itemBlueprint.getId(), baseSell, baseSell);

        for (int i = 0; i < warmupRounds; i++) {
            current = getNextPrice(itemBlueprint, current);
        }
        return current;
    }

    /*
     * Calculate next price following price trends
     * More the price is far from the initial price, more it'll tend to reverse the trend
     * Shock market will add real chaos on a pure random basis
     */
    public static PriceMovement getNextPrice(ItemBlueprint itemBlueprint, PriceMovement priceHistory){
        PriceCoefficients priceCoefficients = priceCoefficientsMap.get(itemBlueprint.getRarity());
        float volatility = priceCoefficients.volatility();
        float momentumCoefficient = priceCoefficients.momentum();
        float reversionCoefficient = priceCoefficients.reversion();

        int newPrice;
        int baseSell = itemBlueprint.getPrice();
        int momentum  = (int) ((priceHistory.currentPrice() - priceHistory.previousPrice()) * momentumCoefficient);
        int reversion = (int) ((baseSell - priceHistory.currentPrice()) * reversionCoefficient);
        int noise     = (int) (RandomGenerator.getDefault().nextInt(-1,1) * volatility * baseSell);
        newPrice = priceHistory.currentPrice() + momentum + reversion + noise;
        newPrice += getMarketShockValue(baseSell);

        int floor = (int) (baseSell * priceCoefficients.minPriceRatio());
        newPrice = Math.max(floor, newPrice);

        return new PriceMovement(itemBlueprint.getId(), newPrice, priceHistory.currentPrice());
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
