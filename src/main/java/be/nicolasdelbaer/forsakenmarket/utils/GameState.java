package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.exceptions.market.UndefinedMarketPriceException;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@ApplicationScoped
public class GameState {

    @Setter
    private Map<Long, PriceMovementByRound> marketPriceList = new HashMap<>();
    @Setter
    private List<ItemBlueprint> itemBlueprintList = new ArrayList<>();

    @Getter
    private Long currentRound = 0L;


    public void nextRound(){
        currentRound++;
    }

    public PriceMovementByRound getPriceHistory(Long blueprintId) throws UndefinedMarketPriceException {
        if(!marketPriceList.containsKey(blueprintId))
            throw new UndefinedMarketPriceException("Market Price not found, missing init?");
        return marketPriceList.get(blueprintId);
    }

    public List<ItemBlueprint> getItemBlueprintList() {
        return Collections.unmodifiableList(itemBlueprintList);
    }
}