package be.nicolasdelbaer.forsakenmarket.schedulers;

import be.nicolasdelbaer.forsakenmarket.broadcaster.BroadcastEventQueue;
import be.nicolasdelbaer.forsakenmarket.broadcaster.EventBroadcaster;
import be.nicolasdelbaer.forsakenmarket.entities.GameState;
import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.EndOfDayBroadcast;
import be.nicolasdelbaer.forsakenmarket.models.broadcast.NewRoundBroadcast;
import be.nicolasdelbaer.forsakenmarket.repositories.GameStateRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemBlueprintRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.MarketPriceRepository;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledInventoryService;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledMarketService;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledPlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.MarketPriceUtils;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class MarketTickerScheduler{
    private static final Logger log = LoggerFactory.getLogger(MarketTickerScheduler.class);
    private ScheduledExecutorService scheduler;
    @Inject private GameStateManager gameStateManager;
    @Inject private EntityManagerFactory entityManagerFactory;
    @Inject private EventBroadcaster eventBroadcaster;
    @Inject private BroadcastEventQueue broadcastEventQueue;

    @Inject private GameStateRepository gameStateRepository;
    @Inject private MarketPriceRepository marketPriceRepository;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;

    @Inject private ScheduledMarketService scheduledMarketService;
    @Inject private ScheduledInventoryService scheduledInventoryService;
    @Inject private ScheduledPlayerService scheduledPlayerService;


    public void onStart(@Observes @Priority(GameConfiguration.SCHEDULER_PRIORITY) @Initialized(ApplicationScoped.class) Object obj) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);

            GameState data = gameStateRepository.getData(entityManager);

            Map<Long, MarketPrice> marketPriceList = marketPriceRepository
                    .findByRoundId(entityManager, data.getCurrentRound())
                    .stream()
                    .collect(Collectors.toMap(
                            marketPrice -> marketPrice.getItemBlueprint().getId(),
                            Function.identity()
                    ));

            gameStateManager.startup(
                    data,
                    marketPriceList,
                    blueprintList.stream()
                        .collect(Collectors.toMap(
                                ItemBlueprint::getId,
                                itemBlueprint -> itemBlueprint
                ))
            );

            startScheduler();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    private void startScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::tick,
                0,
                GameConfiguration.ROUND_DURATION_SECONDS,
                TimeUnit.SECONDS
        );
    }

    public void onStop(@Observes @Destroyed(ApplicationScoped.class) Object e) {
        scheduler.shutdown();
    }


    private void tick() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                long newRoundId = gameStateManager.getCurrentRound() +1;

                //Prices will change towards their trends. Trends will be updated.
                Map<Long, MarketPrice> updatedPrices = MarketPriceUtils.updatePrices(
                        gameStateManager.getItemBlueprintList(),
                        gameStateManager.getMarketPriceMap(),
                        newRoundId
                );

                scheduledMarketService.updateMarketPrices(
                        entityManager,
                        updatedPrices.values().stream().toList()
                );

                //Check & handle items expiration
                scheduledMarketService.updateTimeToLive(entityManager);
                scheduledInventoryService.updateExpirationTime(entityManager);

                //Populate new items if some slots are missing,
                //using gameConfiguration to setup a pool of max available items
                //shared for all players (- bought or rerolled items)
                scheduledMarketService.refreshMarket(entityManager, gameStateManager.getItemBlueprintList());

                if(newRoundId % GameConfiguration.ROUNDS_BY_CYCLE == 0) {
                    scheduledMarketService.recordMarketPriceMovements(entityManager,
                            (newRoundId - GameConfiguration.ROUNDS_BY_CYCLE),
                            GameConfiguration.ROUNDS_BY_CYCLE
                    );
                    //Give salary to players
                    scheduledPlayerService.itsPayday(entityManager);

                    broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToAll(
                            BroadcastEvent.EndOfDay,
                            new EndOfDayBroadcast(true))
                    );
                }
                gameStateRepository.update(entityManager, new GameState(newRoundId));
                transaction.commit();

                //handle next round after all db actions avoiding desync state
                gameStateManager.syncGameStateSnapshot(updatedPrices, newRoundId);
                log.info("Current round: %s".formatted(newRoundId));

                broadcastEventQueue.enqueue(() -> eventBroadcaster.broadcastToAll(
                        BroadcastEvent.NewRound,
                        new NewRoundBroadcast(newRoundId))
                );
                broadcastEventQueue.flush();
            } catch (Exception e) {
                broadcastEventQueue.discard();
                transaction.rollback();
                log.error(e.getMessage(), e);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

}