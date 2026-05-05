package be.nicolasdelbaer.forsakenmarket.models.inventory;

public record CollectionItemResponse(
        Long blueprintId,
        String title,
        String description,
        String icon,
        String rarity,
        Boolean isCollected
) {}
