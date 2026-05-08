package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record CollectedItemBroadcast(
        Long blueprintId,
        String title,
        String description,
        String rarity
) implements BroadcastPayload {}
