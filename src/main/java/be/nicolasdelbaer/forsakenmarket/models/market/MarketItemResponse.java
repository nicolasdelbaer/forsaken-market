package be.nicolasdelbaer.forsakenmarket.models.market;

public record MarketItemResponse(
        Long marketId,
        String title,
        String description,
        String icon,
        String rarity,
        Integer currentPrice
) {}
