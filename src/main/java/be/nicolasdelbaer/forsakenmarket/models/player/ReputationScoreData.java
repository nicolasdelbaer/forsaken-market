package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;

public record ReputationScoreData(BoughtItem boughtItem, MarketPriceHistory marketPrice) {}
