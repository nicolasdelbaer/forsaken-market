package be.nicolasdelbaer.forsakenmarket.models.player;

public record LeaderboardResponse(
        int playerId,
        int score,
        int wallet,
        String name

) { }
