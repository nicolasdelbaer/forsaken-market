package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.Player;

public record PlayerResponse(
    Integer id,
    String email,
    String name,
    Integer wallet,
    Integer level,
    Integer totReput,
    Integer currentReput
) {

    public static PlayerResponse fromPlayer(Player player){
        return new PlayerResponse(
                player.getId(),
                player.getEmail(),
                player.getName(),
                player.getWallet(),
                player.getLevel(),
                player.getTotReput(),
                player.getCurrentReput()
        );
    }

}
