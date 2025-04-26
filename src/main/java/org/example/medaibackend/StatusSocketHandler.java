package org.example.medaibackend;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class StatusSocketHandler extends TextWebSocketHandler {

    // Store all open sessions
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    public void broadcastStatus(String status) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(status));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
