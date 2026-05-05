package be.nicolasdelbaer.forsakenmarket.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.exceptions.auth.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.MissingEnvConfigurationException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerLoginException;
import be.nicolasdelbaer.forsakenmarket.models.player.LeaderboardRepsonse;
import be.nicolasdelbaer.forsakenmarket.models.player.LoginRequestDto;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.models.player.RegisterPlayerRequest;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRoleRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;

@ApplicationScoped
public class PlayerService {

    @Inject private PlayerRepository playerRepository;
    @Inject private EntityManager entityManager;
    @Inject
    private GameStateManager gameStateManager;
    @Inject
    private PlayerRoleRepository playerRoleRepository;

    public PlayerService() {
    }


    @Transactional
    public void register(RegisterPlayerRequest registerPlayerRequest) throws EmailAlreadyUsedException, MissingEnvConfigurationException {
        if(playerRepository.emailExists(entityManager, registerPlayerRequest.email()))
            throw new EmailAlreadyUsedException("Cannot create this player, the email already exists");

        String envCost = System.getenv("BCRYPT_COST");
        if(Objects.isNull(envCost))
            throw new MissingEnvConfigurationException("Cost config is missing");
        int cost = Integer.parseInt(envCost);

        Player player = new Player(
            registerPlayerRequest.userName(),
            registerPlayerRequest.email(),
            BCrypt.withDefaults().hashToString(cost, registerPlayerRequest.password().toCharArray()),
            GameConfiguration.startingWallet,
            List.of(playerRoleRepository.findByRoleName(entityManager,"Merchant"))
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


    public Integer getWallet(Integer playerId) {
        return playerRepository.getWallet(entityManager, playerId);
    }

    public List<LeaderboardRepsonse> fetchLeaderboard(int limit) {
        return playerRepository
                .fetchPlayerScores(entityManager, limit);
    }
}
