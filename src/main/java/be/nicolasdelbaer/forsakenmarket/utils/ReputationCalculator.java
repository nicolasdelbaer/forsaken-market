package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;

public class ReputationCalculator {
    public static Integer calculate(ReputationScoreData data) {
        int boughtPrice = data.inventoryItemBoughtPrice();
        int profit = data.marketPriceCurrentPrice() - boughtPrice;
        int result = 0;
        if(boughtPrice != 0)
            result =  Math.max(0, (int)(((double)profit / boughtPrice) * 10));
        return result;
    }
}
