package be.nicolasdelbaer.forsakenmarket.utils;

import be.nicolasdelbaer.forsakenmarket.models.player.ReputationScoreData;

public class ReputationCalculator {
    public static Integer calculate(ReputationScoreData data) {
        Integer boughtPrice = data.boughtItem().getBoughtPrice();
        Integer profit = boughtPrice - data.marketPrice().getCurrentPrice();
        return Math.max(0, (int) (profit / boughtPrice * 10));
    }
}
