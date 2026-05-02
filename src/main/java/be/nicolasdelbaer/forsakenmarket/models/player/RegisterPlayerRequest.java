package be.nicolasdelbaer.forsakenmarket.models.player;

import jakarta.validation.constraints.NotBlank;

public record RegisterPlayerRequest(
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String userName
) { }
