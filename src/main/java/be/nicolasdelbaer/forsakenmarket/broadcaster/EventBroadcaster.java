package be.nicolasdelbaer.forsakenmarket.broadcaster;

import be.nicolasdelbaer.forsakenmarket.enums.BroadcastEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@ApplicationScoped
public class EventBroadcaster {
    private static final Logger log = LoggerFactory.getLogger(EventBroadcaster.class);
    //CopyOnWriteArrayList is thread safe, ideal for few writes and many reads
    //List of client connections
    private final ConcurrentHashMap<Integer, List<SseEventSink>> clientsByPlayer = new ConcurrentHashMap<>();
    private final AtomicReference<Sse> sseRef = new AtomicReference<>();

    @Inject ObjectMapper objectMapper;

    public void register(SseEventSink sink, Sse sse, int playerId){
        sseRef.compareAndSet(null, sse);
        clientsByPlayer.computeIfAbsent(playerId, k -> new CopyOnWriteArrayList<>()).add(sink);
    }

    public void broadcastToPlayer(BroadcastEvent event, Object data, int playerId){
        broadcast(event, data, clientsByPlayer.get(playerId));
    }
    public void broadcastToAll(BroadcastEvent event, Object data){
        broadcast(event, data, clientsByPlayer.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        );
    }

    private void broadcast(BroadcastEvent event, Object data, List<SseEventSink> clients){
        Sse sse = sseRef.get();
        if(sse == null || clients.isEmpty()) return;

        try {
            OutboundSseEvent outboundSseEvent = sse.newEventBuilder()
                    .name(event.name())
                    .data(objectMapper.writeValueAsString(data))
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

        } catch (JsonProcessingException e) {
            log.error("Json error while formatting broadcast dto ", e);
            throw new RuntimeException(e);
        }

    }
}
