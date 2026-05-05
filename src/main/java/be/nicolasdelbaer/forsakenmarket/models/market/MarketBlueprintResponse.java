package be.nicolasdelbaer.forsakenmarket.models.market;

public record MarketBlueprintResponse(
        Long blueprintId,
        String title,
        String description,
        String icon,
        String rarity
) {}
