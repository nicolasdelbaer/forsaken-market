package be.nicolasdelbaer.forsakenmarket.models.market;

public record MarketOHLC(
        Long item_blueprint_id,
        Integer open,
        Integer high,
        Integer low,
        Integer close
) {
}
