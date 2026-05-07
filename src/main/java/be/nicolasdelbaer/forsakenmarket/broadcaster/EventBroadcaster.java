package be.nicolasdelbaer.forsakenmarket.broadcaster;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventBroadcaster {
    //CopyOnWriteArrayList is thread safe, ideal for few writes and many reads
    //List of client connections
    private final ConcurrentHashMap<Integer, List<SseEventSink>> clientsByPlayer = new ConcurrentHashMap<>();
    private final AtomicReference<Sse> sseRef = new AtomicReference<>();

    public void register(SseEventSink sink, Sse sse, int playerId){
        sseRef.compareAndSet(null, sse);
        clientsByPlayer.computeIfAbsent(playerId, k -> new CopyOnWriteArrayList<>()).add(sink);
    }

    public void broadcastToPlayer(String eventName, String data, int playerId){
        broadcast(eventName, data, clientsByPlayer.get(playerId));
    }
    public void broadcastToAll(String eventName, String data){
        broadcast(eventName, data, clientsByPlayer.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );
    }

    private void broadcast(String eventName, String data, List<SseEventSink> clients){
        Sse sse = sseRef.get();
        if(sse == null || clients.isEmpty()) return;

        OutboundSseEvent outboundSseEvent = sse.newEventBuilder()
                .name(eventName)
                .data(data)
                .build();

        //remove old client or send the event
        clients.removeIf(sink -> {
            if(sink.isClosed()) return true;
            try {
                sink.send(outboundSseEvent);
                return false;
            } catch (Exception e) {
                return true;
            }
        });
    }
}
