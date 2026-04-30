package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.Player;

import java.security.Principal;
import java.util.List;

public record PlayerSession (
    Integer id,
    String email,
    String name,
    List<String> roles
) implements Principal {

    public static PlayerSession fromPlayer(Player player){
        return new PlayerSession(
                player.getId(),
                player.getEmail(),
                player.getName(),
                player.getRoles()
        );
    }

    @Override
    public String getName() {
        return name;
    }
}
