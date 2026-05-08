package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

import java.util.List;

public record ExpiredItemsBroadcast(
        List<TradeBroadcast> soldInventoryItemForPrice
) implements BroadcastPayload { }
