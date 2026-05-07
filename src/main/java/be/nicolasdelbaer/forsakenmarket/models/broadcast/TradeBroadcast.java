package be.nicolasdelbaer.forsakenmarket.models.broadcast;

public record TradeBroadcast(
        Long inventoryItemId,
        Integer currentPrice,
        Integer priceDifference
) { }
