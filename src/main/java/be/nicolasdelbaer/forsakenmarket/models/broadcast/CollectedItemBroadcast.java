package be.nicolasdelbaer.forsakenmarket.models.broadcast;

public record CollectedItemBroadcast(
        Long blueprintId,
        String title,
        String description,
        String rarity
) {}
