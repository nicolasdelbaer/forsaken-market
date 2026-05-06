package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.entities.ItemBlueprint;
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
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(50);

        MarketPrice marketPrice = new MarketPrice(100,100, new ItemBlueprint(), 1L);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        // Act
        Integer result = ReputationCalculator.calculate(data);

        // Assert
        assertTrue(result > 0);
    }

    @Test
    void shouldReturnZero_whenNoProfit() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPrice marketPrice = new MarketPrice(100,100, new ItemBlueprint(), 1L);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZero_whenLoss() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPrice marketPrice = new MarketPrice(50,50, new ItemBlueprint(), 1L);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertEquals(0, result);
    }

    @Test
    void shouldNotThrow_whenBoughtPriceIsZero() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(0);

        MarketPrice marketPrice = new MarketPrice(50,50, new ItemBlueprint(), 1L);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        assertDoesNotThrow(() -> ReputationCalculator.calculate(data));
    }

    @Test
    void shouldNotReturnZero_intDivision() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        MarketPrice marketPrice = new MarketPrice(110,110, new ItemBlueprint(), 1L);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertNotEquals(0, result);
    }
}
