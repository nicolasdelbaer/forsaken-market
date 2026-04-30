package be.nicolasdelbaer.forsakenmarket.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerLoginException;
import be.nicolasdelbaer.forsakenmarket.exceptions.PlayerNotFoundException;
import be.nicolasdelbaer.forsakenmarket.models.player.LoginRequestDto;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.models.player.RegisterPlayerDto;
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


    @Transactional
    public void register(RegisterPlayerDto registerPlayerDto) throws EmailAlreadyUsedException {
        if(playerRepository.emailExists(entityManager, registerPlayerDto.email()))
            throw new EmailAlreadyUsedException("Cannot create this player, the email already exists");

        String envCost = System.getenv("BCRYPT_COST");
        int cost = Integer.parseInt(envCost);

        Player player = new Player(
            registerPlayerDto.userName(),
            registerPlayerDto.email(),
            BCrypt.withDefaults().hashToString(cost, registerPlayerDto.pwd().toCharArray()),
            gameConfiguration.getStartingWallet()
        );

        playerRepository.save(entityManager, player);
    }

    public PlayerSession login(LoginRequestDto loginRequestDto) throws PlayerLoginException {
        if(!playerRepository.emailExists(entityManager, loginRequestDto.email()))
            throw new PlayerLoginException("Incorrect password or login");

        Player player = playerRepository.findByEmail(entityManager, loginRequestDto.email());

        if(!BCrypt.verifyer().verify(loginRequestDto.password().toCharArray(), player.getPassword()).verified)
            throw new PlayerLoginException("Incorrect password or login");

        return PlayerSession.fromPlayer(player);
    }

    @Transactional
    public void addReputation(Integer playerId, Integer reputationScore) throws PlayerNotFoundException {
        Player player = playerRepository.findById(entityManager, playerId).orElseThrow(() -> new PlayerNotFoundException(""));
        player.addReputation(reputationScore);
        playerRepository.save(entityManager, player);
    }
}
