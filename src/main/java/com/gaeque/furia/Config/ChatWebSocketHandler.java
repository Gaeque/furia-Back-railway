package com.gaeque.furia.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaeque.furia.DTOs.ChatMessage;
import com.gaeque.furia.Entity.Profile;
import com.gaeque.furia.Entity.User;
import com.gaeque.furia.Repository.ProfileRepository;
import com.gaeque.furia.Repository.UserRepository;
import com.gaeque.furia.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getToken(session);

        if (token != null && jwtUtil.validateToken(token)) {
            String email = jwtUtil.getUsernameFromToken(token);
            Optional<User> optionalUser = userRepository.findByEmail(email);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                Optional<Profile> profileOptional = profileRepository.findByUser(user);

                if (profileOptional.isPresent()) {
                    Profile profile = profileOptional.get();
                    sessions.put(user.getId(), session); // Salve com o ID do usuário
                    System.out.println("Usuário conectado via WebSocket: " + profile.getUserName());
                } else {
                    System.out.println("Profile não encontrado para o usuário: " + email);
                    session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Perfil não encontrado"));
                }
            } else {
                System.out.println("Usuário não encontrado: " + email);
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Usuário inválido"));
            }
        } else {
            System.out.println("Token inválido ou ausente.");
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Token inválido"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Mensagem recebida: " + message.getPayload());

        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        Long receiverUserId = Long.parseLong(chatMessage.getReceiverId()); // Conversão de String para Long
        WebSocketSession receiverSession = sessions.get(receiverUserId);  // Usar ID do usuário (não do perfil)

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            System.out.println("Mensagem enviada para usuário ID " + receiverUserId);
        } else {
            System.out.println("Usuário ID " + receiverUserId + " está offline. Mensagem não entregue.");
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.values().remove(session);
        System.out.println("Sessão encerrada: " + status);
    }

    private String getToken(WebSocketSession session) {
        String headerToken = session.getHandshakeHeaders().getFirst("Authorization");
        if (headerToken != null && headerToken.startsWith("Bearer ")) {
            return headerToken.replace("Bearer ", "");
        }

        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("token=")) {
            return query.substring("token=".length());
        }

        return null;
    }

}
