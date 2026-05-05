package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.GameState;
import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedBlueprintException;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@ApplicationScoped
public class GameStateManager {

    /*
     * Cached List of Market prices by BlueprintId
     * Long param: Blueprint id
     */
    @Setter
    private Map<Long, PriceMovementByRound> marketPriceList = new HashMap<>();
    /*
     * Cached List of Item Blueprints by id
     * Long param: Blueprint id
     */
    @Setter
    private Map<Long, ItemBlueprint> itemBlueprintList = new HashMap<>();

    @Getter
    private Long currentRound = 0L;


    public void nextRound(){
        currentRound++;
    }

    public PriceMovementByRound getPriceHistory(Long itemBlueprintId) throws UndefinedMarketPriceException {
        if(!marketPriceList.containsKey(itemBlueprintId))
            throw new UndefinedMarketPriceException("Market Price not found, missing init?");
        return marketPriceList.get(itemBlueprintId);
    }

    public List<ItemBlueprint> getItemBlueprintList() {
        return itemBlueprintList.values().stream().toList();
    }

    public ItemBlueprint getItemBlueprint(Long itemBlueprintId) throws UndefinedBlueprintException {
        if(!marketPriceList.containsKey(itemBlueprintId))
            throw new UndefinedBlueprintException("Blueprint not found, missing init?");
        return itemBlueprintList.get(itemBlueprintId);
    }

    public GameState getGameStateEntity() {
        GameState gameState = new GameState();
        gameState.setCurrentRound(currentRound);
        return gameState;
    }

    public void setGameStateEntity(GameState data) {
        currentRound = data.getCurrentRound();
    }
}