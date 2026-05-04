package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.models.market.MarketPriceHistory;
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
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(50);

        MarketPriceHistory marketPrice = new MarketPriceHistory(100,100);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        // Act
        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        // Assert
        assertTrue(result > 0);
    }

    @Test
    void shouldReturnZero_whenNoProfit() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPriceHistory marketPrice = new MarketPriceHistory(100,100);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZero_whenLoss() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPriceHistory marketPrice = new MarketPriceHistory(50,50);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);
        log.debug("Result: {}",result);

        assertEquals(0, result);
    }

    @Test
    void shouldNotThrow_whenBoughtPriceIsZero() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(0);

        MarketPriceHistory marketPrice = new MarketPriceHistory(50,50);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        assertDoesNotThrow(() -> ReputationCalculator.calculate(data));
    }

    @Test
    void shouldNotReturnZero_intDivision() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPriceHistory marketPrice = new MarketPriceHistory(110,110);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertNotEquals(0, result);
    }
}
