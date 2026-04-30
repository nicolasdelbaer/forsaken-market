package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.BoughtItem;
import be.nicolasdelbaer.forsakenmarket.entities.MarketPrice;
import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class ReputationCalculatorTest {
    private static final Logger log = LoggerFactory.getLogger(ReputationCalculatorTest.class);

    @Test
    void shouldReturnPositiveReputation_whenProfitable() {
        // Arrange
        BoughtItem boughtItem = new BoughtItem();
        boughtItem.setBoughtPrice(100);

        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setCurrentPrice(50); // vendu moins cher que acheté → profit

        ReputationScoreData data = new ReputationScoreData(boughtItem, marketPrice);

        // Act
        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        // Assert
        assertTrue(result > 0);
    }

    @Test
    void shouldReturnZero_whenNoProfit() {
        BoughtItem boughtItem = new BoughtItem();
        boughtItem.setBoughtPrice(100);

        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setCurrentPrice(100); // vendu au même prix → pas de profit

        ReputationScoreData data = new ReputationScoreData(boughtItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZero_whenLoss() {
        BoughtItem boughtItem = new BoughtItem();
        boughtItem.setBoughtPrice(50);

        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setCurrentPrice(100); // vendu plus cher → perte

        ReputationScoreData data = new ReputationScoreData(boughtItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        assertEquals(0, result);
    }

    @Test
    void shouldNotThrow_whenBoughtPriceIsZero() {
        BoughtItem boughtItem = new BoughtItem();
        boughtItem.setBoughtPrice(0);

        MarketPrice marketPrice = new MarketPrice();
        marketPrice.setCurrentPrice(50);

        ReputationScoreData data = new ReputationScoreData(boughtItem, marketPrice);

        assertDoesNotThrow(() -> ReputationCalculator.calculate(data));
    }
}
