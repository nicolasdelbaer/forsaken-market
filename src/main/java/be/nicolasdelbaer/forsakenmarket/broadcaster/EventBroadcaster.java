package be.nicolasdelbaer.forsakenmarket.broadcaster;

import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import be.nicolasdelbaer.forsakenmarket.interfaces.BroadcastPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventBroadcaster {
    private static final Logger log = LoggerFactory.getLogger(EventBroadcaster.class);

    //CopyOnWriteArrayList is thread safe, ideal for few writes and many reads; List of client connections
    private final ConcurrentHashMap<Integer, List<SseEventSink>> clientsByPlayer = new ConcurrentHashMap<>();
    private final AtomicReference<Sse> sseRef = new AtomicReference<>();

    @Inject ObjectMapper objectMapper;

    public Map<Integer, Integer> getConnectionsPeek(){
        return clientsByPlayer.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        v -> v.getValue().size()
        ));
    }

    public void register(SseEventSink sink, Sse sse, int playerId){
        sseRef.compareAndSet(null, sse);
        clientsByPlayer.computeIfAbsent(playerId, k -> new CopyOnWriteArrayList<>()).add(sink);
    }

    /*
     * Direct broadcast to player sinks from their id
     */
    public void broadcastToPlayer(BroadcastEvent event, BroadcastPayload data, int playerId){
        List<SseEventSink> clients = clientsByPlayer.get(playerId);
        if(clients == null || clients.isEmpty()) return; //no check on contains because of the concurrentMap
        broadcast(event, data, Map.of(playerId, clients));
    }

    /*
     * Direct broadcast to every registered player sinks
     * Used for global events such as the end of a day
     */
    public void broadcastToAll(BroadcastEvent event, BroadcastPayload data){
        broadcast(event, data, clientsByPlayer);
    }

    private void broadcast(BroadcastEvent event, BroadcastPayload data, Map<Integer, List<SseEventSink>> clients){
        Sse sse = sseRef.get();
        if(sse == null || clients.isEmpty()) return;
        try {
            OutboundSseEvent outboundSseEvent = sse.newEventBuilder()
                    .name(event.name())
                    .data(objectMapper.writeValueAsString(data))
                    .build();

            for (Map.Entry<Integer, List<SseEventSink>> entry : clients.entrySet()) {
                //remove old client or send the event
                List<SseEventSink> sseEventSinks = clientsByPlayer.get(entry.getKey());

                //When player disconnect and the list is empty, the map is cleaned to obnly keep active connections.
                clientsByPlayer.computeIfPresent(entry.getKey(),
                    (k, sinkList) -> {
                        sinkList.removeIf(sink -> {
                            if(sink.isClosed()) return true;
                            try {
                                sink.send(outboundSseEvent);
                                return false;
                            } catch (Exception e) {
                                return true;
                            }
                        });
                        return sinkList.isEmpty() ? null : sinkList;
                });
            }

        } catch (JsonProcessingException e) {
            log.error("Json error while formatting broadcast dto ", e);
            throw new RuntimeException(e);
        }
    }
}
