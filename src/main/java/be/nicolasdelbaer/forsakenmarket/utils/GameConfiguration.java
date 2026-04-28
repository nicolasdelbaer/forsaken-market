package be.nicolasdelbaer.forsakenmarket.utils;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;

@ApplicationScoped
public class GameConfiguration {

    public static final int DataFeedPriority = 5;
    public static final int SchedulerPriority = 10;

    @Getter private final int roundDurationSeconds = 45;
    @Getter private final int startingWallet = 500;


    @Getter private final int rerollCost = 20;
    @Getter private final int maxRerollsPerRound = 3;

    public GameConfiguration() {
    }

}
