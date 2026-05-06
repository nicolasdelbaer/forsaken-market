package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovement;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketPriceUtils {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceUtils.class);

    public static MarketPrice getMarketPrice(ItemBlueprint blueprint, PriceMovement nextPrice, long roundId) {
        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setPreviousPrice(nextPrice.previousPrice());
        marketPrice.setCurrentPrice(nextPrice.currentPrice());
        marketPrice.setItemBlueprint(blueprint);
        marketPrice.setRoundId(roundId);
        return marketPrice;
    }

    public static Map<Long, MarketPrice> updatePrices(
            List<ItemBlueprint> itemBlueprintList, Map<Long, MarketPrice> marketPriceMap, long roundId) {
        Map<Long, MarketPrice> updatedPrices = new HashMap<>();

        for (ItemBlueprint blueprint : itemBlueprintList) {
            PriceMovement newPrice = calculateNextPrice(blueprint, marketPriceMap.get(blueprint.getId()));
            updatedPrices.put(
                    blueprint.getId(),
                    new MarketPrice(
                            newPrice.currentPrice(),
                            newPrice.previousPrice(),
                            blueprint,
                            roundId
            ));
        }
        return updatedPrices;
    }

    private static PriceMovement calculateNextPrice(ItemBlueprint blueprint, @NotNull MarketPrice marketPrice) {
        PriceMovement priceMovement = new PriceMovement(
                marketPrice.getItemBlueprint().getId(),
                marketPrice.getCurrentPrice(),
                marketPrice.getPreviousPrice()
        );
        return PricesCalculator.getNextPrice(blueprint, priceMovement);
    }
}
