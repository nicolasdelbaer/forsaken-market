package be.nicolasdelbaer.forsakenmarket.schedulers;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import be.nicolasdelbaer.forsakenmarket.repositories.GameStateRepository;
import be.nicolasdelbaer.forsakenmarket.repositories.ItemBlueprintRepository;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledInventoryService;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledMarketService;
import be.nicolasdelbaer.forsakenmarket.services.scheduled.ScheduledPlayerService;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameStateManager;
import be.nicolasdelbaer.forsakenmarket.utils.PricesCalculator;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Inject private GameStateRepository gameStateRepository;
    @Inject private GameStateManager gameStateManager;
    @Inject
    private ScheduledPlayerService scheduledPlayerService;

    public void onStart(@Observes @Priority(GameConfiguration.SchedulerPriority) @Initialized(ApplicationScoped.class) Object obj) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            gameStateManager.setGameStateEntity(gameStateRepository.getData(entityManager));
            fillMarketWithPrices(entityManager);
            gameStateManager.setItemBlueprintList(itemBlueprintRepository
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


    private void fillMarketWithPrices(EntityManager entityManager) {
        Map<Long, PriceMovementByRound> marketPriceList = new HashMap<>();
        List<ItemBlueprint> blueprintList = itemBlueprintRepository.findAll(entityManager);

        for (ItemBlueprint blueprint : blueprintList) {
            marketPriceList.put(
                    blueprint.getId(),
                    PricesCalculator.warmupPrice(blueprint)
            );
        }
        gameStateManager.setMarketPriceList(marketPriceList);
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
                gameStateManager.nextRound();

                //Prices will change towards their trends. Trends will be updated.
                scheduledMarketService.updateMarketPrices(entityManager);
                //Check & handle market items expiration
                scheduledMarketService.updateTimeToLive(entityManager);
                //Check & handle inventory item decay; they'll lost value once decayed
                scheduledInventoryService.updateDecay(entityManager);

                //Populate new items if some slots are missing,
                //using gameConfiguration to setup a pool of max available items
                //shared for all players (- bought or rerolled items)
                scheduledMarketService.refreshMarket(entityManager, gameStateManager.getItemBlueprintList());
                System.out.printf("Current round: %s%n", gameStateManager.getCurrentRound());

                if(gameStateManager.getCurrentRound() % GameConfiguration.roundsByCycle == 0) {
                    scheduledMarketService.recordMarketPriceMovements(entityManager,
                            (gameStateManager.getCurrentRound() - GameConfiguration.roundsByCycle),
                            GameConfiguration.roundsByCycle
                    );
                    //Give 50 bucks salary to players
                    scheduledPlayerService.itsPayday(entityManager);
                }
                if(gameStateManager.getCurrentRound() % GameConfiguration.roundsBeforeClean == 0) {
                    cleanupData();
                }
                gameStateRepository.update(entityManager, gameStateManager.getGameStateEntity());
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