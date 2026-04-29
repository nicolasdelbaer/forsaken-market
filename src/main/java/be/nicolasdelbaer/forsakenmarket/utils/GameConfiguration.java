package be.nicolasdelbaer.forsakenmarket.utils;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@ApplicationScoped
public class GameConfiguration {

    //Annotations
    public static final int DataFeedPriority = 5;
    public static final int SchedulerPriority = 10;

    //Meta rules
    @Getter private final int roundDurationSeconds = 45;
    @Getter private final int startingWallet = 500;

    //Market rules
    @Getter private final int marketPoolSize = 20;
    @Getter private final int maxRerollsPerRound = 3;
    @Getter private final int rerollCost = 20;

    public GameConfiguration() {
    }
}
