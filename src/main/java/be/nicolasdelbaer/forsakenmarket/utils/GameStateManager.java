package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.GameState;
import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedBlueprintException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.models.GameStateSnapshot;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class GameStateManager {

    private final AtomicReference<GameStateSnapshot> gameStateSnapshot =
            new AtomicReference<>(new GameStateSnapshot( Map.of(), Map.of(), List.of(), 1L));

    private volatile Instant lastRoundStartedAt = null;

    public void startup(GameState data, Map<Long, MarketPrice> marketPriceList, Map<Long, ItemBlueprint> blueprints) {
        gameStateSnapshot.updateAndGet (snapshot ->
                new GameStateSnapshot(
                        marketPriceList,
                        blueprints,
                        blueprints.values().stream().toList(),
                        data.getCurrentRound())
        );
    }

    public void syncGameStateSnapshot(Map<Long, MarketPrice> marketPriceList, long newRoundId){
        lastRoundStartedAt = Instant.now();
        gameStateSnapshot.updateAndGet (snapshot ->
                new GameStateSnapshot(
                        marketPriceList,
                        snapshot.itemBlueprintMap(),
                        snapshot.itemBlueprintList(),
                        newRoundId
                )
        );
    }

    public int getSecondsUntilNextRound() {
        if (lastRoundStartedAt == null) return GameConfiguration.ROUND_DURATION_SECONDS;
        long elapsed = Duration.between(lastRoundStartedAt, Instant.now()).toSeconds();
        return (int) Math.max(0, GameConfiguration.ROUND_DURATION_SECONDS - elapsed);
    }

    public long getCurrentRound(){
        return gameStateSnapshot.get().currentRound();
    }

    public List<ItemBlueprint> getItemBlueprintList() {
        return List.copyOf(gameStateSnapshot.get().itemBlueprintList());
    }
    public Map<Long, MarketPrice> getMarketPriceMap() {
        return Map.copyOf(gameStateSnapshot.get().marketPriceMap());
    }
    public MarketPrice getCurrentMarketPrice(Long itemBlueprintId) throws UndefinedMarketPriceException {
        MarketPrice result = gameStateSnapshot.get().marketPriceMap().get(itemBlueprintId);
        if(result == null) throw new UndefinedMarketPriceException();
        return result;
    }

    public ItemBlueprint getItemBlueprint(Long itemBlueprintId) throws UndefinedBlueprintException {
        ItemBlueprint blueprint = gameStateSnapshot.get().itemBlueprintMap().get(itemBlueprintId);
        if(blueprint == null) throw new UndefinedBlueprintException();
        return blueprint;
    }

    public GameState toGameStateEntity() {
        GameStateSnapshot snapshot = gameStateSnapshot.get();
        GameState gameState = new GameState();
        gameState.setCurrentRound(snapshot.currentRound());
        return gameState;
    }
}