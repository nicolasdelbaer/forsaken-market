package be.nicolasdelbaer.forsakenmarket.utils;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GameState {

    private Long currentRound = 0L;

    public Long getCurrentRound() {
        return currentRound;
    }

    public void nextRound(){
        currentRound++;
    }
}
