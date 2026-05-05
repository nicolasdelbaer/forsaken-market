package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.entities.InventoryItem;
import be.nicolasdelbaer.forsakenmarket.models.market.PriceMovementByRound;
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

        PriceMovementByRound marketPrice = new PriceMovementByRound(1, 100,100);

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

        PriceMovementByRound marketPrice = new PriceMovementByRound(1, 100,100);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZero_whenLoss() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        PriceMovementByRound marketPrice = new PriceMovementByRound(1, 50,50);

        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertEquals(0, result);
    }

    @Test
    void shouldNotThrow_whenBoughtPriceIsZero() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(0);

        PriceMovementByRound marketPrice = new PriceMovementByRound(1, 50,50);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        assertDoesNotThrow(() -> ReputationCalculator.calculate(data));
    }

    @Test
    void shouldNotReturnZero_intDivision() {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setBoughtPrice(100);

        PriceMovementByRound marketPrice = new PriceMovementByRound(1, 110,110);
        ReputationScoreData data = new ReputationScoreData(inventoryItem, marketPrice);

        Integer result = ReputationCalculator.calculate(data);

        assertNotEquals(0, result);
    }
}
