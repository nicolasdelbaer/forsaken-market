package be.nicolasdelbaer.forsakenmarket.models.player;

public record LeaderboardRepsonse(
        int playerId,
        int score,
        int wallet,
        String name

) { }
