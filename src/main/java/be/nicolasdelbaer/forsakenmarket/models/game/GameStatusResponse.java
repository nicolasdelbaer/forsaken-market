package be.nicolasdelbaer.forsakenmarket.models.game;

public record GameStatusResponse(
        long currentRound,
        int secondsUntilNextRound,
        int roundDurationSeconds,
        int roundsByCycle
) {}
