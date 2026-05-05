package be.nicolasdelbaer.forsakenmarket.utils;

public record PriceCoefficients(float momentum, float reversion, float volatility, float minPriceRatio) { }
