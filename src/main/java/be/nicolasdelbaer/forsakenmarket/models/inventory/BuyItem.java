package be.nicolasdelbaer.forsakenmarket.models.inventory;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;

public record BuyItem(
        Player player,
        MarketItem marketItem,
        PriceMovementByRound marketPrice,
        Long roundId) {
}