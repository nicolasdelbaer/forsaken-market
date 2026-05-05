package be.nicolasdelbaer.forsakenmarket.models.market;

/*
 * Used for sending data to usersy
 */
public record MarketOHLCResponse(
        Integer open,
        Integer high,
        Integer low,
        Integer close
) {
}
