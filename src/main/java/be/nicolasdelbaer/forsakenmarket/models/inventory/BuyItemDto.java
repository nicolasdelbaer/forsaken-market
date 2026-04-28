package be.nicolasdelbaer.forsakenmarket.models.inventory;

import be.nicolasdelbaer.forsakenmarket.entities.MarketItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.entities.Player;

public record BuyItemDto(Player player, MarketItem marketItem, MarketPrice marketPrice, Long roundId) {
}