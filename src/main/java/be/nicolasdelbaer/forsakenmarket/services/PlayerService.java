package be.nicolasdelbaer.forsakenmarket.services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import be.nicolasdelbaer.forsakenmarket.annotations.Transactional;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.exceptions.auth.EmailAlreadyUsedException;
import be.nicolasdelbaer.forsakenmarket.exceptions.core.MissingEnvConfigurationException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerInsufficientFundsException;
import be.nicolasdelbaer.forsakenmarket.exceptions.player.PlayerLoginException;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.ReputationUpdateBroadcast;
import be.nicolasdelbaer.forsakenmarket.models.player.LeaderboardResponse;
import be.nicolasdelbaer.forsakenmarket.models.player.LoginRequest;
import be.nicolasdelbaer.forsakenmarket.models.player.PlayerSession;
import be.nicolasdelbaer.forsakenmarket.models.player.RegisterPlayerRequest;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRoleRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class PlayerService {

    @Inject private PlayerRepository playerRepository;
    @Inject private EntityManager entityManager;
    @Inject private PlayerRoleRepository playerRoleRepository;
    @Inject private EventBroadcaster eventBroadcaster;

    public PlayerService() {
    }


    @Transactional
    public void register(RegisterPlayerRequest registerPlayerRequest)
            throws EmailAlreadyUsedException, NumberFormatException, MissingEnvConfigurationException {
        if(playerRepository.emailExists(entityManager, registerPlayerRequest.email()))
            throw new EmailAlreadyUsedException("Cannot create this player, the email already exists");

        String envCost = System.getenv("BCRYPT_COST");
        if(envCost == null)
            throw new MissingEnvConfigurationException("Cost config is missing");

        int cost = Integer.parseInt(envCost);

        Player player = new Player(
            registerPlayerRequest.userName(),
            registerPlayerRequest.email(),
            BCrypt.withDefaults().hashToString(cost, registerPlayerRequest.password().toCharArray()),
            GameConfiguration.STARTING_WALLET,
            List.of(playerRoleRepository.findByRoleName(entityManager,"Merchant"))
        );

        playerRepository.save(entityManager, player);
    }

    public PlayerSession login(LoginRequest loginRequest) throws PlayerLoginException {
        if(!playerRepository.emailExists(entityManager, loginRequest.email()))
            throw new PlayerLoginException("Incorrect password or login");

        Player player = playerRepository.findByEmail(entityManager, loginRequest.email());

        if(!BCrypt.verifyer().verify(loginRequest.password().toCharArray(), player.getPassword()).verified)
            throw new PlayerLoginException("Incorrect password or login");

        return PlayerSession.fromPlayer(player);
    }

    public Integer getWallet(Integer playerId) {
        return playerRepository.getWallet(entityManager, playerId);
    }

    public List<LeaderboardResponse> fetchLeaderboard(int limit) {
        return playerRepository
                .fetchPlayerScores(entityManager, limit);
    }

    public void addReputation(Player player, Integer reputationScore) {
        int currentReput = player.getCurrentReput();
        int currentLevel = player.getLevel();

        player.addReputation(reputationScore);

        eventBroadcaster.broadcastToPlayer(
                BroadcastEvent.LevelUp,
                new ReputationUpdateBroadcast(
                        currentReput,
                        player.getCurrentReput(),
                        currentLevel,
                        player.getLevel()
                ), player.getId()
        );
    }

    public void debit(Player player, Integer amount) throws PlayerInsufficientFundsException {
        player.debit(amount);
    }
    public void credit(Player player, Integer amount){
        player.credit(amount);
    }

    public boolean canAfford(Player player, Integer cost){
        return cost <= player.getWallet();
    }

}
