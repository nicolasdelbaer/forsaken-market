package be.nicolasdelbaer.forsakenmarket.models.player;

import jakarta.validation.constraints.NotBlank;

public record RegisterPlayerDto(
        @NotBlank String email,
        @NotBlank String pwd,
        @NotBlank String userName
) { }
