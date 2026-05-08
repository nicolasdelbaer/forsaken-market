package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record NewLevelBroadcast(
        Integer value
) implements BroadcastPayload { }
