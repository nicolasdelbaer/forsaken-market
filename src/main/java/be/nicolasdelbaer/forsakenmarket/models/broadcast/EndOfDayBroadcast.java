package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record EndOfDayBroadcast(
        Boolean isTriggered
) implements BroadcastPayload { }
