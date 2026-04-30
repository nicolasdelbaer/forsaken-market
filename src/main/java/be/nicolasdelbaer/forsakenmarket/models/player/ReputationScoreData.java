package be.nicolasdelbaer.forsakenmarket.models.player;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;

public record ReputationScoreData(BoughtItem boughtItem, MarketPrice marketPrice) {}
