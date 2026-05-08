package be.nicolasdelbaer.forsakenmarket.broadcaster;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class BroadcastEventQueue {

    /**
     * No @RequestScope because the scheduler is out of he CDI context
     * Using threads will fix the issue creating
     *  Thread REST (player A)    →  pending = [event1, event2]
     *  Thread REST (player B)    →  pending = [event3]
     *  Thread Scheduler (tick)   →  pending = [event4, event5, event6]
     */
    private final ThreadLocal<List<Runnable>> pending =
            ThreadLocal.withInitial(ArrayList::new);

    public void enqueue(Runnable broadcast) {
        pending.get().add(broadcast);
    }
    public void flush() {
        pending.get().forEach(Runnable::run);
        pending.get().clear();
    }
    public void discard() {
        pending.get().clear();
    }
}
