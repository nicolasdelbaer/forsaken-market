package be.nicolasdelbaer.forsakenmarket.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.models.player.CreateUserDto;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class PlayerService {

    @Inject private PlayerRepository playerRepository;
    @Inject private GameConfiguration gameConfiguration;
    @Inject private EntityManager entityManager;

    public PlayerService() {
    }


    public Player register(CreateUserDto createUserDto) throws EmailAlreadyUsedException {
        if(playerRepository.emailExists(entityManager, createUserDto.email()))
            throw new EmailAlreadyUsedException("Cannot create this player, the email already exists");

        String envCost = System.getenv("BCRYPT_COST");
        int cost = Integer.parseInt(envCost);

        Player player = new Player(
            createUserDto.userName(),
            createUserDto.email(),
            BCrypt.withDefaults().hashToString(cost, createUserDto.pwd().toCharArray()),
            gameConfiguration.getStartingWallet()
        );

        playerRepository.save(entityManager, player);
        return player;
    }

    public void addReputation(Player player, Integer reputationScore){
        player.addReputation(reputationScore);
    }
}
