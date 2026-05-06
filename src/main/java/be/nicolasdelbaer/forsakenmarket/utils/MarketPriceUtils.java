package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
