package be.nicolasdelbaer.forsakenmarket.schedulers;

import be.nicolasdelbaer.forsakenmarket.services.MarketService;
import be.nicolasdelbaer.forsakenmarket.utils.GameConfiguration;
import be.nicolasdelbaer.forsakenmarket.utils.GameState;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class MarketTickerScheduler implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Inject private MarketService marketService;
    @Inject private GameState gameState;

    public void onStart(@Observes @Priority(GameConfiguration.SchedulerPriority) @Initialized(ApplicationScoped.class) Object e) {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::tick,
                0,
                45,
                TimeUnit.SECONDS
        );
    }

    public void onStop(@Observes @Destroyed(ApplicationScoped.class) Object e) {
        scheduler.shutdown();
    }

    private void tick() {

        //Change cycle
        gameState.nextRound();

        //TODO - Update stocks & trends

        //TODO - Update item ttl

        //TODO - Update item decay


        //Refresh market
        marketService.refreshMarket();

        System.out.printf("Current round: %s%n", gameState.getCurrentRound());
    }
}