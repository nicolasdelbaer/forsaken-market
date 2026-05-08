package be.nicolasdelbaer.forsakenmarket.models.broadcast;

import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;

public record ReputationUpdateBroadcast(
        Integer fromReput,
        Integer toReput,
        Integer initialLevel,
        Integer newLevel
) implements BroadcastPayload { }
