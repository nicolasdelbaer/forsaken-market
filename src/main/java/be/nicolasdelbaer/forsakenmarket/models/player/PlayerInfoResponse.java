package be.nicolasdelbaer.forsakenmarket.models.player;

public record PlayerInfoResponse(
    Integer id,
    String email,
    String name,
    Integer wallet
) {}
