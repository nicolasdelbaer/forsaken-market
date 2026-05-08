package be.nicolasdelbaer.forsakenmarket.utils;

public class GameConfiguration {

    //Annotations
    public static final int DATA_FEED_PRIORITY = 5;
    public static final int SCHEDULER_PRIORITY = 10;

    //Meta rules
    public static final int ROUND_DURATION_SECONDS = 42;
    public static final int STARTING_WALLET = 500;
    public static final int ROUNDS_BY_CYCLE = 5; //5 for testing, 20 by design
    public static final int ROUNDS_BEFORE_CLEAN = 100;
    public static final int PAYDAY_AMOUNT = 10;

    //Market rules
    public static final int MARKET_POOL_SIZE = 20;
    public static final int MAX_REROLLS_PER_ROUND = 3;
    public static final int REROLL_COST = 20;
    public static final int MARKET_VISIBLE_LIMIT = 8;

    public static final int OHLC_RANGE = 50;

}
