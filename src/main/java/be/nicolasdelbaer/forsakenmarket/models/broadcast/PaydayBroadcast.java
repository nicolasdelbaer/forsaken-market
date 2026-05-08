package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record PaydayBroadcast(
        Integer value
) implements BroadcastPayload { }
