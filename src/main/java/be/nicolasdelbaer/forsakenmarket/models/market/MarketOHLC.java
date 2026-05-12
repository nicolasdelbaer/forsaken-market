package be.nicolasdelbaer.forsakenmarket.models.market;

/*
 * Used for internal calculations and creating MarketPriceEvolution
 */
public record MarketOHLC(
        Long item_blueprint_id,
        Integer open,
        Integer high,
        Integer low,
        Integer close
) { }
