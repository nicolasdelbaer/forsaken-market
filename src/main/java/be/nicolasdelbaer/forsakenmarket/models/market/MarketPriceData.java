package be.nicolasdelbaer.forsakenmarket.models.market;

public record MarketPriceData(
        Long id,
        Integer previousPrice,
        Integer currentPrice
) { }
