package be.nicolasdelbaer.forsakenmarket.models.player;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
