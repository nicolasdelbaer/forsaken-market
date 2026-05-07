package be.nicolasdelbaer.forsakenmarket.broadcaster;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class EventBroadcaster {
    //CopyOnWriteArrayList is thread safe, ideal for few writes and many reads
    //List of client connections
    private final List<SseEventSink> clients = new CopyOnWriteArrayList<>();
    private final AtomicReference<Sse> sseRef = new AtomicReference<>();

    public void register(SseEventSink sink, Sse sse){
        sseRef.compareAndSet(null, sse);
        clients.add(sink);
    }

    public void broadcast(String eventName, String data){
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
