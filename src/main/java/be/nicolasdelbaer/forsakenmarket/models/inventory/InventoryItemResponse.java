package be.nicolasdelbaer.forsakenmarket.models.inventory;

public record InventoryItemResponse(
        Long inventoryId,
        Long blueprintId,
        String title,
        String description,
        String icon,
        String rarity,
        Integer boughtPrice,
        Integer soldPrice,
        Integer currentPrice,
        Boolean isDecayed
) {}
