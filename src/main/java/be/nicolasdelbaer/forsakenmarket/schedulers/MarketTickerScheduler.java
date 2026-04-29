package be.nicolasdelbaer.forsakenmarket.schedulers;

import be.nicolasdelbaer.forsakenmarket.services.InventoryService;
import be.nicolasdelbaer.forsakenmarket.services.MarketService;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MarketTickerScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;
    @Inject private EntityManagerFactory entityManagerFactory;

    @Inject private MarketService marketService;
    @Inject private InventoryService inventoryService;
    @Inject private GameState gameState;
    @Inject
    private GameConfiguration gameConfiguration;

    public void onStart(@Observes @Priority(GameConfiguration.SchedulerPriority) @Initialized(ApplicationScoped.class) Object e) {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::tick,
                0,
                gameConfiguration.getRoundDurationSeconds(),
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
                marketService.updateMarketPrices(entityManager);
                //Check & handle market items expiration
                marketService.updateTimeToLive(entityManager);
                //Check & handle inventory item decay; they'll lost value once decayed
                inventoryService.updateDecay(entityManager);

                //Populate new items if some slots are missing,
                //using gameConfiguration to setup a pool of max available items
                //shared for all players (- bought or rerolled items)
                marketService.refreshMarket(entityManager);
                System.out.printf("Current round: %s%n", gameState.getCurrentRound());
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace(); //TODO use logger
            }
        }

        if(gameState.getCurrentRound() % 50 == 0)
            cleanupData();
    }

    private void cleanupData() {
        //TODO clear unused data
    }

}