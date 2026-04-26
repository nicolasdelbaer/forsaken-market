package be.nicolasdelbaer.forsakenmarket.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.models.player.CreateUserDto;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PlayerService {

    @Inject
    PlayerRepository playerRepository;

    public PlayerService() {
    }


    public Player register(CreateUserDto createUserDto) throws EmailAlreadyUsedException {

        if(playerRepository.emailExists(createUserDto.email()))
            throw new EmailAlreadyUsedException("Cannot create this player, the email already exists");

        String envCost = System.getenv("BCRYPT_COST");
        int cost = Integer.parseInt(envCost);

        Player player = new Player();
        player.setName(createUserDto.userName());
        player.setWallet(createUserDto.userWallet());
        player.setEmail(createUserDto.email());
        player.setPassword(BCrypt.withDefaults().hashToString(cost, createUserDto.pwd().toCharArray()));

        playerRepository.save(player);
        return player;
    }
}
