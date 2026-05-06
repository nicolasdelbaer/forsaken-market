package be.nicolasdelbaer.forsakenmarket.models;

import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;

import java.util.List;
import java.util.Map;

public record GameStateSnapshot(
    Map<Long, MarketPrice> marketPriceMap,
    Map<Long, ItemBlueprint> itemBlueprintMap,
    List<ItemBlueprint> itemBlueprintList,
    Long currentRound
) { }
