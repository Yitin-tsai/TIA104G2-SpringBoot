package chilltrip.chat.controller;

import java.io.IOException;
import java.util.Set;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import chilltrip.message.model.ChatMessage;

@Controller
public class MessageController {
	
    @MessageMapping("/chat")  // 用來處理來自 /app/chat 的消息
    @SendTo("/topic/messages")
    protected void handleTextMessage( ChatMessage chatMessage) throws InterruptedException, IOException {
        
        System.out.println("Received message: " + chatMessage.getMessage());
        Set<WebSocketSession>  connectedSessions = ChatRoomController.getConnectedSession();
        ObjectMapper objectMapper = new ObjectMapper();
        // 根據 receiver 發送訊息給指定的客戶端
        for (WebSocketSession s : connectedSessions) {
            if (s.isOpen() && s.getAttributes().get("userName").equals(chatMessage.getReceiver())) {
                // 只有發送給 receiver 的會話
                String response = objectMapper.writeValueAsString(chatMessage);
                s.sendMessage(new TextMessage(response)); // 發送消息
                System.out.println("Sent message to: " + chatMessage.getReceiver());
            }
        }
    }
}
