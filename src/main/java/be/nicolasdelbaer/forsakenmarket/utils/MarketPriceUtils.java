package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.ForsakenRuntimeException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketPriceUtils {

    private static final Logger log = LoggerFactory.getLogger(MarketPriceUtils.class);

    public static PriceMovementByRound getMarketRoundMovement(GameState gameState, ItemBlueprint itemBlueprint) {
        try {
            return gameState.getPriceHistory(itemBlueprint.getId());
        } catch (UndefinedMarketPriceException e) {
            log.error("Cannot find market price with blueprint id %s: ".formatted(itemBlueprint.getId()));
            throw new ForsakenRuntimeException(e.getMessage());
        }
    }
}
