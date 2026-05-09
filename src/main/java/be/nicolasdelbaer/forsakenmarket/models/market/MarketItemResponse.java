package be.nicolasdelbaer.forsakenmarket.models.market;

public record MarketItemResponse(
        Long marketId,
        Long blueprintId,
        String title,
        String description,
        String icon,
        String rarity,
        Integer currentPrice
) {}
