package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import java.util.List;

public record ExpiredItemsBroadcast(
        List<TradeBroadcast> soldInventoryItemForPrice
) { }
