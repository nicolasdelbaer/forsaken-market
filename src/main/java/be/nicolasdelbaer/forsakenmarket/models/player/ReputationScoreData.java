package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;

public record ReputationScoreData(InventoryItem inventoryItem, PriceMovementByRound marketPrice) {}
