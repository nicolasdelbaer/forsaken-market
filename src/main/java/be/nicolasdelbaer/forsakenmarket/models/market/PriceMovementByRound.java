package be.nicolasdelbaer.forsakenmarket.models.market;

public record PriceMovementByRound(long blueprintId, int currentPrice, int previousPrice) {
}
