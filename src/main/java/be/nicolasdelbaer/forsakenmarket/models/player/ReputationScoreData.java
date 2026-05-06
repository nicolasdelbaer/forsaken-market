package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;

public record ReputationScoreData(InventoryItem inventoryItem, MarketPrice marketPrice) {}
