package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;

public record ReputationScoreData(InventoryItem inventoryItem, MarketPriceHistory marketPrice) {}
