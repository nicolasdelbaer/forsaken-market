package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record NewRoundBroadcast(
        Long roundCounter
) implements BroadcastPayload { }
