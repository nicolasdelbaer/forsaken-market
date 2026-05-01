package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class GameState {

    private Map<Long, MarketPriceHistory> marketPriceList = new HashMap<>();

    private Long currentRound = 0L;

    public Long getCurrentRound() {
        return currentRound;
    }
    public void nextRound(){
        currentRound++;
    }


    public void setPrices(Map<Long, MarketPriceHistory> marketPriceList) {
        this.marketPriceList = marketPriceList;
    }

    public MarketPriceHistory getPriceHistory(Long blueprintId) {
        return marketPriceList.get(blueprintId);
    }

    public Map<Long, MarketPriceHistory> getPricesHistory() {
        return Collections.unmodifiableMap(marketPriceList);
    }
}