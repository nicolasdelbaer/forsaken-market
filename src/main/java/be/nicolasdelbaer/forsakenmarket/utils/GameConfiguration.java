package be.nicolasdelbaer.forsakenmarket.utils;

public class GameConfiguration {

    //Annotations
    public static final int DataFeedPriority = 5;
    public static final int SchedulerPriority = 10;

    //Meta rules
    public static final int roundDurationSeconds = 1;
    public static final int startingWallet = 500;
    public static final int roundsByCycle = 5; //5 for testing, 20 by design
    public static final int roundsBeforeClean = 100;
    public static final int paydayAmount = 10;

    //Market rules
    public static final int marketPoolSize = 20;
    public static final int maxRerollsPerRound = 3;
    public static final int rerollCost = 20;
    public static final int marketVisibleLimit = 8;

    public static final int OHLC_range = 50;
}
