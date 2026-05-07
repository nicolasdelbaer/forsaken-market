package be.nicolasdelbaer.forsakenmarket.services.scheduled;

import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.PaydayBroadcast;
import be.nicolasdelbaer.forsakenmarket.repositories.PlayerRepository;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;


/*
 * Executed by scheduler -> need to pass the entityManager to methods
 */
@ApplicationScoped
public class ScheduledPlayerService {

    @Inject private PlayerRepository playerRepository;
    @Inject private EventBroadcaster eventBroadcaster;

    /*
     * Player Salary day consists on:
     *   - add fix amount of money into players wallet
     */
    public void itsPayday(EntityManager entityManager) {
        playerRepository.addSalaryToPlayers(entityManager, GameConfiguration.PAYDAY_AMOUNT);
        eventBroadcaster.broadcastToAll(BroadcastEvent.Payday, new PaydayBroadcast(GameConfiguration.PAYDAY_AMOUNT));
    }



}
