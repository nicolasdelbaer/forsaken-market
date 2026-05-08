package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record TradeBroadcast(
        Long inventoryItemId,
        Integer currentPrice,
        Integer priceDifference
) implements BroadcastPayload { }
