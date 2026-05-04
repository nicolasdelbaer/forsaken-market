package be.nicolasdelbaer.forsakenmarket.schedulers;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemBlueprintRepository;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledInventoryService;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledMarketService;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class MarketTickerScheduler implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(MarketTickerScheduler.class);
    private ScheduledExecutorService scheduler;
    @Inject private EntityManagerFactory entityManagerFactory;
    @Inject private ScheduledMarketService scheduledMarketService;
    @Inject private ScheduledInventoryService scheduledInventoryService;
    @Inject private ItemBlueprintRepository itemBlueprintRepository;
    @Inject private GameState gameState;

    public void onStart(@Observes @Priority(GameConfiguration.SchedulerPriority) @Initialized(ApplicationScoped.class) Object obj) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            gameState.setItemBlueprintList(itemBlueprintRepository
                    .findAll(entityManager).stream()
                    .collect(Collectors.toMap(
                            ItemBlueprint::getId,
                            itemBlueprint -> itemBlueprint
                    )));

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
                GameConfiguration.roundDurationSeconds,
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
                //Change cycle
                gameState.nextRound();

                //Prices will change towards their trends. Trends will be updated.
                scheduledMarketService.updateMarketPrices(entityManager);
                //Check & handle market items expiration
                scheduledMarketService.updateTimeToLive(entityManager);
                //Check & handle inventory item decay; they'll lost value once decayed
                scheduledInventoryService.updateDecay(entityManager);

                //Populate new items if some slots are missing,
                //using gameConfiguration to setup a pool of max available items
                //shared for all players (- bought or rerolled items)
                scheduledMarketService.refreshMarket(entityManager, gameState.getItemBlueprintList());
                System.out.printf("Current round: %s%n", gameState.getCurrentRound());

                if(gameState.getCurrentRound() % GameConfiguration.roundsByCycle == 0) {
                    scheduledMarketService.recordMarketPriceMovements(entityManager,
                            (gameState.getCurrentRound()- GameConfiguration.roundsByCycle),
                            GameConfiguration.roundsByCycle
                    );
                }
                if(gameState.getCurrentRound() % GameConfiguration.roundsBeforeClean == 0) {
                    cleanupData();
                }
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                log.error(e.getMessage(), e);
            }
        }

    }

    private void cleanupData() {
        //TODO clear unused data
    }

}