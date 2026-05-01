package be.nicolasdelbaer.forsakenmarket.models.inventory;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.Player;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;

public record BuyItemDto(
        Player player,
        MarketItem marketItem,
        MarketPriceHistory marketPrice,
        Long roundId) {
}