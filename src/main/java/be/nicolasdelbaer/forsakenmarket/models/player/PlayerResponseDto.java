package be.nicolasdelbaer.forsakenmarket.models.player;

public record PlayerResponseDto(
    Integer id,
    String email,
    String name,
    Integer wallet
) {}
