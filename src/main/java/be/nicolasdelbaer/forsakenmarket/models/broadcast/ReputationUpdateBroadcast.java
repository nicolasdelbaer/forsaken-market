package be.nicolasdelbaer.forsakenmarket.models.broadcast;

public record ReputationUpdateBroadcast(
        Integer fromReput,
        Integer toReput,
        Integer initialLevel,
        Integer newLevel
) { }
